package de.twometer.neko.render

import de.twometer.neko.util.Hash
import de.twometer.neko.util.HashResult
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

class UniformBuffer(size: Int) {

    internal val bufferId: Int = glGenBuffers()
    private var buffer: ByteBuffer = BufferUtils.createByteBuffer(size)
    private var savedPos = 0
    private var savedLimit = 0

    private fun saveState() {
        savedLimit = buffer.limit()
        savedPos = buffer.position()
    }

    private fun restoreState() {
        buffer.limit(savedLimit)
        buffer.position(savedPos)
    }

    private fun tryFlip() {
        if (buffer.position() != 0)
            buffer.flip()
    }

    fun bind() = glBindBuffer(GL_UNIFORM_BUFFER, bufferId)

    fun unbind() = glBindBuffer(GL_UNIFORM_BUFFER, 0)

    fun rewind() {
        buffer.rewind()
    }

    fun upload() {
        saveState()
        tryFlip()
        glBufferData(GL_UNIFORM_BUFFER, buffer, GL_DYNAMIC_DRAW)
        restoreState()
    }

    fun hash(): HashResult {
        saveState()
        tryFlip()
        val hash = Hash.meow(buffer)
        restoreState()
        return hash
    }

    fun writeFloat(value: Float) {
        buffer.putFloat(value)
    }

    fun writeVec4(vector: Vector4f) {
        buffer.putFloat(vector.x)
        buffer.putFloat(vector.y)
        buffer.putFloat(vector.z)
        buffer.putFloat(vector.w)
    }

    fun writeMat4(matrix: Matrix4f) {
        MemoryStack.stackPush().use {
            val tmp = it.mallocFloat(16)
            matrix.get(tmp)
            for (i in 0..15) buffer.putFloat(tmp.get(i))
        }
    }

}