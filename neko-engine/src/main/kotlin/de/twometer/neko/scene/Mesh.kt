package de.twometer.neko.scene

import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(private val capacity: Int, val dimensions: Int) {

    val vertices: FloatBuffer = MemoryUtil.memAllocFloat(capacity * dimensions)
    var normals: FloatBuffer? = null
    var colors: FloatBuffer? = null
    var texCoords: FloatBuffer? = null
    var indices: IntBuffer? = null

    var numVertices = 0
        private set
    var numNormals = 0
        private set
    var numColors = 0
        private set
    var numTexCoords = 0
        private set

    fun addNormals(): Mesh {
        normals = MemoryUtil.memAllocFloat(capacity * dimensions)
        return this
    }

    fun addColors(dim: Int = 3): Mesh {
        colors = MemoryUtil.memAllocFloat(capacity * dim)
        return this
    }

    fun addTexCoords(dim: Int = 2): Mesh {
        texCoords = MemoryUtil.memAllocFloat(capacity * dim)
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

    fun destroy() {
        MemoryUtil.memFree(vertices)
        colors?.apply(MemoryUtil::memFree)
        normals?.apply(MemoryUtil::memFree)
        texCoords?.apply(MemoryUtil::memFree)
    }

    fun toGeometry(): Geometry {
        return Geometry(this).also { destroy() }
    }

}