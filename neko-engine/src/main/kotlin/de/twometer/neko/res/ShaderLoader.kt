package de.twometer.neko.res

import de.twometer.neko.render.Shader
import de.twometer.neko.render.ShaderInject
import de.twometer.neko.render.ShaderProperty
import de.twometer.neko.res.ShaderParser.SECTION_FRAGMENT
import de.twometer.neko.res.ShaderParser.SECTION_SHARED
import de.twometer.neko.res.ShaderParser.SECTION_VERTEX
import mu.KotlinLogging
import org.lwjgl.opengl.GL20.*

private val logger = KotlinLogging.logger {}

object ShaderLoader {

    fun load(path: String): Shader {
        logger.info { "Loading nks shader $path" }

        val ast = ShaderParser.loadShaderAst(path)

        var vertexShader = ast.getSectionContent(SECTION_VERTEX)
        var fragmentShader = ast.getSectionContent(SECTION_FRAGMENT)

        when {
            vertexShader == null -> failure("Vertex shader not defined")
            fragmentShader == null -> failure("Fragment shader not defined")
        }

        ast.sections[SECTION_SHARED]?.forEach {
            if (it is ShaderParser.PlainText) {
                vertexShader = "out ${it.data}\n$vertexShader"
                fragmentShader = "in ${it.data}\n$fragmentShader"
            }
        }

        vertexShader = "#version ${ast.version}\n$vertexShader"
        fragmentShader = "#version ${ast.version}\n$fragmentShader"

        val shader = Shader(loadGlsl(vertexShader!!, fragmentShader!!))

        shader.bind()
        ast.directives.forEach {
            when (it.type) {
                ShaderParser.DirectiveType.Bind -> {
                    shader[it.arguments[0]] = it.arguments[1].toInt()
                }
                ShaderParser.DirectiveType.Inject -> {
                    shader.injects.add(ShaderInject.valueOf(it.arguments.first()))
                }
                ShaderParser.DirectiveType.Set -> {
                    shader.properties[ShaderProperty.valueOf(it.arguments[0])] = it.arguments[1]
                }
                else -> {
                }
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
        checkShaderErrors(vertexShaderId, vertexSrc)

        glShaderSource(fragmentShaderId, fragmentSrc)
        glCompileShader(fragmentShaderId)
        checkShaderErrors(fragmentShaderId, fragmentSrc)

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

    private fun checkShaderErrors(shaderId: Int, src: String) {
        val log = glGetShaderInfoLog(shaderId)
        if (log.isNotEmpty()) {
            val logLines = log.split("\n")

            logger.error { "Shader compilation failed" }
            logLines.forEach {
                if (it.isNotBlank() && it.contains("error"))
                    logger.error { reformatLogLine(it, src) }
            }

            failure("Could not compile GLSL")
        }
    }

    private fun reformatLogLine(logLine: String, src: String): String {
        val glslLine = logLine.substringAfter("(").substringBefore(")").toInt()
        val glslErrorMessage = logLine.substringAfter(':').trim()

        val nksRefInfo = src.split("\n")[glslLine - 1].substringAfterLast("REF:").split(":")
        val nksFileName = nksRefInfo[0]
        val nksLineNo = nksRefInfo[1]

        return "[$nksFileName:$nksLineNo] $glslErrorMessage"
    }

    private fun failure(message: String): Nothing {
        error("Shader compilation error: $message")
    }

}