package de.twometer.neko.res

import de.twometer.neko.render.Shader
import mu.KotlinLogging
import org.lwjgl.opengl.GL20.*
import java.io.File

private val logger = KotlinLogging.logger {}

object ShaderLoader {

    private const val SECTION_SHARED = "shared"
    private const val SECTION_VERTEX = "vertex"
    private const val SECTION_FRAGMENT = "fragment"

    open class Node
    data class PlainText(val data: String) : Node()
    data class Directive(val type: DirectiveType, val arguments: List<String>) : Node()

    data class ShaderAst(
        val version: String,
        val sections: HashMap<String, List<Node>>,
        val directives: List<Directive>
    ) {
        fun getSectionContent(key: String): String? {
            return sections[key]?.joinToString(separator = "\n") { if (it is PlainText) it.data else "" }
        }
    }

    enum class DirectiveType {
        Version,
        Include,
        Begin,
        End,
        Bind,
        Tag,
        Set
    }

    private fun parseDirectiveType(name: String): DirectiveType = when (name.toLowerCase()) {
        "version" -> DirectiveType.Version
        "include" -> DirectiveType.Include
        "begin" -> DirectiveType.Begin
        "end" -> DirectiveType.End
        "bind" -> DirectiveType.Bind
        "tag" -> DirectiveType.Tag
        "set" -> DirectiveType.Set
        else -> failure("Unknown directive $name")
    }

    private fun parseShader(file: File): List<Node> {
        val result = ArrayList<Node>()

        var lineNo = 0
        file.forEachLine {
            val line = it.trim()
            lineNo++

            when {
                line.startsWith('#') -> {
                    if (line.length <= 1)
                        failure("Shader parser error (${file.name}:$lineNo): expected instruction")
                    if (!line.contains(' '))
                        failure("Shader parser error (${file.name}:$lineNo): expected parameter")

                    val content = line.substring(1)
                    val directive = parseDirectiveType(content.substringBefore(' '))
                    val param = content.substringAfter(' ').trim().split(' ').filter { arg -> arg.isNotBlank() }
                    result.add(Directive(directive, param))
                }
                line.startsWith("//") -> return@forEachLine
                line.isNotEmpty() -> result.add(PlainText(line))
            }
        }
        return result
    }

    private fun loadIncludes(basePath: String, nodes: List<Node>, depth: Int = 0): List<Node> {
        if (depth > 10)
            failure("Maximum include depth exceeded")
        val result = ArrayList<Node>()
        nodes.forEach {
            if (it is Directive && it.type == DirectiveType.Include) {
                val file = File(basePath, it.arguments.first())
                result.addAll(loadIncludes(basePath, parseShader(file), depth + 1))
            } else
                result.add(it)
        }
        return result
    }

    private fun buildShaderAst(path: String): ShaderAst {
        val shaderFile = AssetManager.resolve(path, AssetType.Shaders)
        val nodes = loadIncludes(shaderFile.parent, parseShader(shaderFile))

        var version = "330 core"
        val sections = HashMap<String, List<Node>>()
        val directives = ArrayList<Directive>()
        var currentSection: ArrayList<Node>? = null

        nodes.forEach {
            when {
                it is Directive -> {
                    when (it.type) {
                        DirectiveType.Begin -> {
                            if (sections.containsKey(it.arguments.first()))
                                failure("Section ${it.arguments.first()} defined twice")

                            currentSection = ArrayList()
                            sections[it.arguments.first()] = currentSection!!
                        }
                        DirectiveType.End -> currentSection = null
                        DirectiveType.Version -> version = it.arguments.first()
                        else -> {
                            if (currentSection == null)
                                directives.add(it)
                            else
                                failure("Illegal directive ${it.type}")
                        }
                    }
                }
                currentSection != null -> currentSection?.add(it)
                else -> failure("Unexpected $it")
            }
        }

        if (currentSection != null)
            failure("Section not closed")

        return ShaderAst(version, sections, directives)
    }

    fun loadFromFile(path: String): Shader {
        logger.info { "Loading nks shader $path" }

        val ast = buildShaderAst(path)

        var vertexShader = ast.getSectionContent(SECTION_VERTEX)
        var fragmentShader = ast.getSectionContent(SECTION_FRAGMENT)

        when {
            vertexShader == null -> failure("Vertex shader not defined")
            fragmentShader == null -> failure("Fragment shader not defined")
        }

        ast.sections[SECTION_SHARED]?.forEach {
            if (it is PlainText) {
                vertexShader = "out ${it.data}\n$vertexShader"
                fragmentShader = "in ${it.data}\n$fragmentShader"
            }
        }

        vertexShader = "#version ${ast.version}\n$vertexShader"
        fragmentShader = "#version ${ast.version}\n$fragmentShader"

        val shader = Shader(loadGlsl(vertexShader!!, fragmentShader!!))

        shader.bind()
        ast.directives.forEach {
            if (it.type == DirectiveType.Bind) {
                shader[it.arguments[0]] = it.arguments[1].toInt()
            } else if (it.type == DirectiveType.Tag) {
                shader.tags.add(it.arguments.first())
            } else if (it.type == DirectiveType.Set) {
                shader.props[it.arguments[0]] = it.arguments[1]
            }
        }
        shader.unbind()

        return shader
    }

    private fun loadGlsl(vertexSrc: String, fragmentSrc: String): Int {
        val vertexShaderId = glCreateShader(GL_VERTEX_SHADER)
        val fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER)

        glShaderSource(vertexShaderId, vertexSrc)
        glCompileShader(vertexShaderId)
        checkShaderError(vertexShaderId)

        glShaderSource(fragmentShaderId, fragmentSrc)
        glCompileShader(fragmentShaderId)
        checkShaderError(fragmentShaderId)

        val programId = glCreateProgram()
        glAttachShader(programId, vertexShaderId)
        glAttachShader(programId, fragmentShaderId)
        glLinkProgram(programId)

        // After the shader program is linked, the shader sources can be cleaned up
        glDetachShader(programId, vertexShaderId)
        glDetachShader(programId, fragmentShaderId)
        glDeleteShader(vertexShaderId)
        glDeleteShader(fragmentShaderId)

        return programId
    }

    private fun checkShaderError(shaderId: Int) {
        val log = glGetShaderInfoLog(shaderId)
        if (log.isNotEmpty())
            failure(log)
    }

    private fun failure(message: String): Nothing {
        error("Shader compilation error: $message")
    }

}