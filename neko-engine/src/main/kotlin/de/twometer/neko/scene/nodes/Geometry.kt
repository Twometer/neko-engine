package de.twometer.neko.scene.nodes

import de.twometer.neko.render.Shader
import de.twometer.neko.scene.AABB
import de.twometer.neko.scene.Bone
import de.twometer.neko.scene.Material
import de.twometer.neko.scene.Mesh
import de.twometer.neko.scene.component.BoundingBoxProviderComponent
import de.twometer.neko.scene.component.SkeletonComponent
import org.lwjgl.opengl.GL30.*
import java.nio.Buffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Geometry(name: String, material: Material = Material.Default, private val _aabb: AABB? = null) :
    RenderableNode(name = name, material = material) {

    private var vao: Int = -1
    private var vertexBuffer: Int = -1
    private var normalBuffer: Int? = null
    private var texCoordBuffer: Int? = null
    private var indexBuffer: Int? = null
    private var boneIdBuffer: Int? = null
    private var boneWeightBuffer: Int? = null
    private var numIndices: Int = -1
    private var numVertices: Int = -1
    var canPick: Boolean = true
    val aabb: AABB?
        get() = getComponent<BoundingBoxProviderComponent>()?.provider?.invoke(this) ?: _aabb

    companion object {
        const val VertexIdx = 0
        const val NormalIdx = 1
        const val TexCoordIdx = 2
        const val BoneIdIdx = 3
        const val BoneWeightIdx = 4
    }

    fun initialize(mesh: Mesh) {
        mesh.normals = destroyEmptyBuffer(mesh.normals)
        mesh.texCoords = destroyEmptyBuffer(mesh.texCoords)
        mesh.indices = destroyEmptyBuffer(mesh.indices)
        mesh.boneIds = destroyEmptyBuffer(mesh.boneIds)
        mesh.boneWeights = destroyEmptyBuffer(mesh.boneWeights)

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
        mesh.bones?.let { attachComponent(SkeletonComponent(mesh.bones as Map<String, Bone>)) }

        numIndices = mesh.numIndices
        numVertices = mesh.numVertices

        glBindVertexArray(0)
    }

    override fun createInstance(): Node {
        val node = Geometry(name = name, material = material.createInstance(), _aabb = _aabb)
        node.initializeFrom(this)
        node.vao = vao
        node.vertexBuffer = vertexBuffer
        node.normalBuffer = normalBuffer
        node.texCoordBuffer = texCoordBuffer
        node.indexBuffer = indexBuffer
        node.boneIdBuffer = boneIdBuffer
        node.boneWeightBuffer = boneWeightBuffer
        node.numIndices = numIndices
        node.numVertices = numVertices
        return node
    }

    override fun render(shader: Shader) {
        glBindVertexArray(vao)
        if (indexBuffer != null) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer!!)
            glDrawElements(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        } else
            glDrawArrays(GL_TRIANGLES, 0, numVertices)

        glBindVertexArray(0)
    }

    fun destroy() {
        glDeleteBuffers(vertexBuffer)
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

    private fun <T : Buffer> destroyEmptyBuffer(buffer: T?): T? {
        return if (buffer?.position() != 0)
            buffer
        else
            null
    }

}