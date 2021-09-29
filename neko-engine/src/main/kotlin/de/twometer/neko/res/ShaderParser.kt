package de.twometer.neko.res

import java.io.File

object ShaderParser {

    private const val DEFAULT_VERSION = "330 core"

    const val SECTION_SHARED = "shared"
    const val SECTION_VERTEX = "vertex"
    const val SECTION_FRAGMENT = "fragment"

    open class Node
    data class PlainText(val data: String) : Node()
    data class Directive(val type: DirectiveType, val arguments: List<String>) : Node()

    data class ShaderAst(
        val version: String,
        val sections: HashMap<String, ArrayList<Node>>,
        val directives: List<Directive>
    ) {
        fun getSectionContent(key: String): String? {
            return sections[key]?.joinToString(separator = "\n") { if (it is PlainText) it.data else "" }
        }
    }

    enum class DirectiveType {
        Version,
        Include,
        Inject,
        Begin,
        End,
        Bind,
        Set
    }

    private fun parseDirectiveType(name: String): DirectiveType? = when (name.toLowerCase()) {
        "version" -> DirectiveType.Version
        "include" -> DirectiveType.Include
        "inject" -> DirectiveType.Inject
        "begin" -> DirectiveType.Begin
        "end" -> DirectiveType.End
        "bind" -> DirectiveType.Bind
        "set" -> DirectiveType.Set
        else -> null
    }

    private fun parseShader(file: File): List<Node> {
        val result = ArrayList<Node>()

        var lineNo = 0
        file.forEachLine {
            val line = it.trim()
            lineNo++

            fun copyLineRaw() =
                result.add(PlainText("$line //REF:${file.name}:$lineNo:"))

            when {
                line.startsWith('#') -> {
                    if (line.length <= 1)
                        failure("Shader parser error (${file.name}:$lineNo): expected instruction")

                    val content = line.substring(1)
                    val directive =
                        parseDirectiveType(if (content.contains(' ')) content.substringBefore(' ') else content)

                    if (directive != null) {
                        if (!content.contains(' '))
                            failure("Shader parser error (${file.name}:$lineNo): expected parameter")

                        val param = content.substringAfter(' ').trim().split(' ').filter { arg -> arg.isNotBlank() }
                        result.add(Directive(directive, param))
                    } else copyLineRaw()
                }
                line.startsWith("//") -> return@forEachLine
                line.isNotEmpty() -> copyLineRaw()
            }
        }
        return result
    }

    private fun loadIncludes(nodes: List<Node>, depth: Int = 0): List<Node> {
        if (depth > 10)
            failure("Maximum include depth exceeded")
        val result = ArrayList<Node>()
        nodes.forEach {
            if (it is Directive && it.type == DirectiveType.Include) {
                val path = AssetManager.resolve(it.arguments.first().removeSurrounding("\""), AssetType.Shaders)
                result.addAll(loadIncludes(parseShader(path), depth + 1))
            } else
                result.add(it)
        }
        return result
    }

    fun loadShaderAst(path: String): ShaderAst {
        val shaderFile = AssetManager.resolve(path, AssetType.Shaders)
        val nodes = loadIncludes(parseShader(shaderFile))

        var version = DEFAULT_VERSION
        val sections = HashMap<String, ArrayList<Node>>()
        val directives = ArrayList<Directive>()
        var currentSection: ArrayList<Node>? = null

        nodes.forEach {
            when {
                it is Directive -> {
                    when (it.type) {
                        DirectiveType.Begin -> {
                            val key = it.arguments.first()
                            if (sections.containsKey(key)) {
                                currentSection = sections[key]
                            } else {
                                currentSection = ArrayList()
                                sections[key] = currentSection!!
                            }
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

    private fun failure(message: String): Nothing {
        error("Shader parsing error: $message")
    }

}