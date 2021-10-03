package de.twometer.neko.scene.nodes

import de.twometer.neko.scene.AABB
import de.twometer.neko.scene.Material
import de.twometer.neko.scene.Mesh
import de.twometer.neko.scene.SkeletonNode
import org.lwjgl.opengl.GL30.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Geometry(
    private val mesh: Mesh,
    material: Material = Material.Default,
    name: String = "",
    val skeletonRoot: SkeletonNode? = null,
    val aabb: AABB? = null
) :
    RenderableNode(material, name = name) {

    companion object {
        const val VertexIdx = 0
        const val NormalIdx = 1
        const val TexCoordIdx = 2
        const val BoneIdIdx = 3
        const val BoneWeightIdx = 4
    }

    private val vao: Int
    private val vertexBuffer: Int
    private val normalBuffer: Int?
    private val texCoordBuffer: Int?
    private val indexBuffer: Int?
    private val boneIdBuffer: Int?
    private val boneWeightBuffer: Int?

    init {
        mesh.vertices.flip()
        mesh.normals?.flip()
        mesh.texCoords?.flip()
        mesh.indices?.flip()
        mesh.boneIds?.flip()
        mesh.boneWeights?.flip()

        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        vertexBuffer = createFloatArrayBuffer(VertexIdx, mesh.dimensions, mesh.vertices)
        normalBuffer = mesh.normals?.let { createFloatArrayBuffer(NormalIdx, mesh.dimensions, it) }
        texCoordBuffer = mesh.texCoords?.let { createFloatArrayBuffer(TexCoordIdx, mesh.dimTexCoords, it) }
        indexBuffer = mesh.indices?.let { createIndexBuffer(it) }
        boneIdBuffer = mesh.boneIds?.let { createIntArrayBuffer(BoneIdIdx, 4, it) }
        boneWeightBuffer = mesh.boneWeights?.let { createFloatArrayBuffer(BoneWeightIdx, 4, it) }

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
        normalBuffer?.also { glDeleteBuffers(it) }
        texCoordBuffer?.also { glDeleteBuffers(it) }
        indexBuffer?.also { glDeleteBuffers(it) }
        glDeleteVertexArrays(vao)
    }

    fun findModelNode(): ModelNode {
        var node: Node? = this
        while (node != null) {
            if (node is ModelNode)
                return node
            node = node.parent
        }
        error("Geometry $name did not have a parent ModelNode")
    }

    private fun createIndexBuffer(data: IntBuffer): Int {
        val elementBuffer = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        return elementBuffer
    }

    private fun createFloatArrayBuffer(index: Int, dimensions: Int, data: FloatBuffer): Int {
        val vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW)
        glVertexAttribPointer(index, dimensions, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(index)
        return vbo
    }

    private fun createIntArrayBuffer(index: Int, dimensions: Int, data: IntBuffer): Int {
        val vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW)
        glVertexAttribIPointer(index, dimensions, GL_INT, 0, 0)
        glEnableVertexAttribArray(index)
        return vbo
    }

}