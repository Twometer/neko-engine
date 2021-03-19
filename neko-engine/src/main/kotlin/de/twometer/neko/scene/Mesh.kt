package de.twometer.neko.scene

import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(private val capacity: Int, val dimensions: Int, val name: String = "") {

    val vertices: FloatBuffer = MemoryUtil.memAllocFloat(capacity * dimensions)
    var normals: FloatBuffer? = null
    var texCoords: FloatBuffer? = null
    var indices: IntBuffer? = null

    var numVertices = 0
        private set
    var numNormals = 0
        private set
    var numTexCoords = 0
        private set
    var numIndices = 0
        private set

    var dimTexCoords = 0
        private set

    fun addNormals(): Mesh {
        normals = MemoryUtil.memAllocFloat(capacity * dimensions)
        return this
    }

    fun addTexCoords(dim: Int = 2): Mesh {
        texCoords = MemoryUtil.memAllocFloat(capacity * dim)
        dimTexCoords = dim
        return this
    }

    fun addIndices(capacity: Int): Mesh {
        indices = MemoryUtil.memAllocInt(capacity)
        return this
    }

    fun putVertex(x: Float, y: Float) {
        vertices.put(x)
        vertices.put(y)
        numVertices++
    }

    fun putVertex(x: Float, y: Float, z: Float) {
        vertices.put(x)
        vertices.put(y)
        vertices.put(z)
        numVertices++
    }

    fun putNormal(x: Float, y: Float) {
        normals?.put(x)
        normals?.put(y)
        numNormals++
    }

    fun putNormal(x: Float, y: Float, z: Float) {
        normals?.put(x)
        normals?.put(y)
        normals?.put(z)
        numNormals++
    }

    fun putTexCoord(x: Float, y: Float) {
        texCoords?.put(x)
        texCoords?.put(y)
        numTexCoords++
    }

    fun putTexCoord(x: Float, y: Float, z: Float) {
        texCoords?.put(x)
        texCoords?.put(y)
        texCoords?.put(z)
        numTexCoords++
    }

    fun putIndices(vararg indices: Int) {
        this.indices?.put(indices)
        numIndices += indices.size
    }

    fun destroy() {
        MemoryUtil.memFree(vertices)
        normals?.apply(MemoryUtil::memFree)
        texCoords?.apply(MemoryUtil::memFree)
        indices?.apply(MemoryUtil::memFree)
    }

    fun toGeometry(material: Material = Material.Default): Geometry {
        return Geometry(this, material, name).also { destroy() }
    }

}