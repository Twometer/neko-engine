package de.twometer.neko.scene

import org.lwjgl.opengl.GL30.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Geometry(private val mesh: Mesh, material: Material = Material.Default, val name: String = "") : Renderable(material) {

    companion object {
        const val VertexIdx = 0
        const val ColorIdx = 1
        const val NormalIdx = 2
        const val TexCoordIdx = 3
    }

    private val vao: Int
    private val vertexBuffer: Int
    private val colorBuffer: Int?
    private val normalBuffer: Int?
    private val texCoordBuffer: Int?
    private val indexBuffer: Int?

    init {
        mesh.vertices.flip()
        mesh.colors?.flip()
        mesh.normals?.flip()
        mesh.texCoords?.flip()
        mesh.indices?.flip()

        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        vertexBuffer = createArrayBuffer(VertexIdx, mesh.dimensions, mesh.vertices)
        colorBuffer = mesh.colors?.let { createArrayBuffer(ColorIdx, mesh.dimColors, it) }
        normalBuffer = mesh.normals?.let { createArrayBuffer(NormalIdx, mesh.dimensions, it) }
        texCoordBuffer = mesh.texCoords?.let { createArrayBuffer(TexCoordIdx, mesh.dimTexCoords, it) }
        indexBuffer = mesh.indices?.let { createIndexBuffer(it) }
        glBindVertexArray(0)
    }

    override fun render() {
        glBindVertexArray(vao)
        if (indexBuffer != null) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer)
            glDrawElements(GL_TRIANGLES, mesh.numIndices, GL_UNSIGNED_INT, 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        } else
            glDrawArrays(GL_TRIANGLES, 0, mesh.numVertices)

        glBindVertexArray(0)
    }

    fun destroy() {
        glDeleteBuffers(vertexBuffer)
        colorBuffer?.also { glDeleteBuffers(it) }
        normalBuffer?.also { glDeleteBuffers(it) }
        texCoordBuffer?.also { glDeleteBuffers(it) }
        indexBuffer?.also { glDeleteBuffers(it) }
        glDeleteVertexArrays(vao)
    }

    private fun createIndexBuffer(data: IntBuffer): Int {
        val elementBuffer = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        return elementBuffer
    }

    private fun createArrayBuffer(index: Int, dimensions: Int, data: FloatBuffer): Int {
        val vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW)
        glVertexAttribPointer(index, dimensions, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(index)
        return vbo
    }

}