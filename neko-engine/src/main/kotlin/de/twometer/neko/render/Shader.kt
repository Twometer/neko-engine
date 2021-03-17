package de.twometer.neko.render

import de.twometer.neko.scene.Color
import de.twometer.neko.util.Cache
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryUtil

class Shader(private val programId: Int) {

    private val uniformCache = object : Cache<String, Int>() {
        override fun create(key: String): Int = glGetUniformLocation(programId, key)
    }

    fun bind() {
        glUseProgram(programId)
    }

    fun unbind() {
        glUseProgram(0)
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
        val buf = MemoryUtil.memAllocFloat(16)
        value.get(buf)
        glUniformMatrix4fv(location, false, buf)
        MemoryUtil.memFree(buf)
    }

}