package de.twometer.neko.res

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
    data class Directive(val type: DirectiveType, val argument: String) : Node()

    data class ShaderAst(val version: String, val sections: HashMap<String, List<Node>>) {
        fun getSectionContent(key: String): String? {
            return sections[key]?.joinToString(separator = "\n") { if (it is PlainText) it.data else "" }
        }
    }

    enum class DirectiveType {
        Version,
        Include,
        Begin,
        End
    }

    private fun parseDirectiveType(name: String): DirectiveType = when (name.toLowerCase()) {
        "version" -> DirectiveType.Version
        "include" -> DirectiveType.Include
        "begin" -> DirectiveType.Begin
        "end" -> DirectiveType.End
        else -> failure("Unknown directive $name")
    }

    private fun parseShader(file: File): List<Node> {
        val result = ArrayList<Node>()

        var line = 0
        file.forEachLine {
            line++
            if (it.startsWith('#')) {
                if (it.length <= 1)
                    failure("Shader parser error (${file.name}:$line): expected instruction")
                if (!it.contains(' '))
                    failure("Shader parser error (${file.name}:$line): expected parameter")

                val content = it.substring(1)
                val directive = parseDirectiveType(content.substringBefore(' '))
                val param = content.substringAfter(' ').trim()
                result.add(Directive(directive, param))
            } else if (it.trim().isNotEmpty()) {
                result.add(PlainText(it))
            }
        }
        return result
    }

    private fun loadIncludes(basePath: String, nodes: List<Node>, depth: Int = 0): List<Node> {
        if (depth > 10)
            error("Maximum include depth exceeded")

        val result = ArrayList<Node>()
        nodes.forEach {
            if (it is Directive && it.type == DirectiveType.Include) {
                val file = File(basePath, it.argument)
                result.addAll(loadIncludes(basePath, parseShader(file), depth + 1))
            } else
                result.add(it)
        }
        return result
    }

    private fun buildShaderAst(path: String): ShaderAst {
        val shaderFile = AssetFiles.resolve(path)
        val nodes = loadIncludes(shaderFile.parent, parseShader(shaderFile))

        var version = "330 core"
        val sections = HashMap<String, List<Node>>()
        var currentSection: ArrayList<Node>? = null

        nodes.forEach {
            when {
                it is Directive -> {
                    when (it.type) {
                        DirectiveType.Begin -> {
                            if (sections.containsKey(it.argument))
                                failure("section ${it.argument} defined twice")

                            currentSection = ArrayList()
                            sections[it.argument] = currentSection!!
                        }
                        DirectiveType.End -> currentSection = null
                        DirectiveType.Version -> version = it.argument
                        else -> failure("Illegal directive ${it.type}")
                    }
                }
                currentSection != null -> currentSection?.add(it)
                else -> failure("Unexpected $it")
            }
        }

        if (currentSection != null)
            failure("Section not closed")

        return ShaderAst(version, sections)
    }

    fun loadFromFile(path: String): Int {
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

        return loadGlsl(vertexShader!!, fragmentShader!!)
    }

    fun loadFromFiles(vertex: String, fragment: String): Int {
        logger.info { "Loading glsl shader $vertex $fragment" }
        return loadGlsl(
            File(vertex).readText(),
            File(fragment).readText()
        )
    }

    fun loadGlsl(vertexSrc: String, fragmentSrc: String): Int {
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
        logger.error { "Shader compilation error: $message" }
        error("Shader failed to compile")
    }

}