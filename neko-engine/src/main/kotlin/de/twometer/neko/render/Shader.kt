package de.twometer.neko.render

import de.twometer.neko.scene.Color
import de.twometer.neko.util.Cache
import org.joml.*
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack


class Shader(private val programId: Int) {

    val injects = ArrayList<ShaderInject>()
    val properties = HashMap<ShaderProperty, String>()

    private val uniformBlockCache = object : Cache<String, Int>() {
        override fun create(key: String): Int = glGetUniformBlockIndex(programId, key)
    }
    private val uniformCache = object : Cache<String, Int>() {
        override fun create(key: String): Int = glGetUniformLocation(programId, key)
    }

    fun bind() {
        OpenGL.useProgram(programId)
        injects.forEach { it.inject(this) }
        properties.forEach { (k, v) -> k.apply(v) }
    }

    fun unbind() {
        OpenGL.useProgram(0)
    }

    fun bindUniformBuffer(blockName: String, buffer: UniformBuffer, bindingPoint: Int) {
        val index = uniformBlockCache.get(blockName)
        glUniformBlockBinding(programId, index, bindingPoint)
        glBindBufferBase(GL_UNIFORM_BUFFER, bindingPoint, buffer.bufferId)
    }

    operator fun set(name: String, value: Float) = glUniform1f(uniformCache.get(name), value)
    operator fun set(name: String, value: Int) = glUniform1i(uniformCache.get(name), value)
    operator fun set(name: String, value: Boolean) = glUniform1i(uniformCache.get(name), if (value) 1 else 0)
    operator fun set(name: String, value: Vector2f) = glUniform2f(uniformCache.get(name), value.x, value.y)
    operator fun set(name: String, value: Vector3f) = glUniform3f(uniformCache.get(name), value.x, value.y, value.z)
    operator fun set(name: String, value: Vector4f) =
        glUniform4f(uniformCache.get(name), value.x, value.y, value.z, value.w)

    operator fun set(name: String, value: Color) =
        glUniform4f(uniformCache.get(name), value.r, value.g, value.b, value.a)

    operator fun set(name: String, value: Matrix4f) {
        val location = uniformCache.get(name)
        MemoryStack.stackPush().use {
            val buf = it.mallocFloat(16)
            value.get(buf)
            glUniformMatrix4fv(location, false, buf)
        }
    }

    operator fun set(name: String, value: Matrix3f) {
        val location = uniformCache.get(name)
        MemoryStack.stackPush().use {
            val buf = it.mallocFloat(9)
            value.get(buf)
            glUniformMatrix3fv(location, false, buf)
        }
    }

}