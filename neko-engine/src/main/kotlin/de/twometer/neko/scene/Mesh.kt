package de.twometer.neko.scene

import de.twometer.neko.scene.nodes.Geometry
import org.joml.Vector3f
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(private val capacity: Int, val dimensions: Int, val name: String = "Unnamed mesh") {

    companion object {
        private const val AABB_INIT = 100000000f
        const val MAX_BONE_INFLUENCE = 4
    }

    val aabb: AABB = AABB(Vector3f(AABB_INIT, AABB_INIT, AABB_INIT), Vector3f(-AABB_INIT, -AABB_INIT, -AABB_INIT))
    val vertices: FloatBuffer = MemoryUtil.memAllocFloat(capacity * dimensions)
    var normals: FloatBuffer? = null
    var texCoords: FloatBuffer? = null
    var indices: IntBuffer? = null
    var boneIds: IntBuffer? = null
    var boneWeights: FloatBuffer? = null
    var bones: MutableMap<String, Bone>? = null

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

    fun addBones(): Mesh {
        boneIds = MemoryUtil.memAllocInt(capacity * 4)
        boneWeights = MemoryUtil.memAllocFloat(capacity * 4)
        bones = HashMap()

        // Fill rig with default values
        for (i in 0 until capacity * MAX_BONE_INFLUENCE) {
            boneIds!!.put(-1)
            boneWeights!!.put(0.0f)
        }

        return this
    }

    fun hasBones() = boneIds != null && boneWeights != null && bones != null

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
        aabb.addPoint(Vector3f(x, y, 0f))
        vertices.put(x)
        vertices.put(y)
        numVertices++
    }

    fun putVertex(x: Float, y: Float, z: Float) {
        aabb.addPoint(Vector3f(x, y, z))
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

    fun putBone(bone: Bone) {
        if (!hasBones())
            throw IllegalStateException("Cannot put bone data to mesh without a rig")

        this.bones!![bone.name] = bone

        bone.vertexWeights.forEach {
            val baseOffset = it.vertexId * MAX_BONE_INFLUENCE
            for (i in 0 until MAX_BONE_INFLUENCE) {
                if (boneIds!!.get(baseOffset + i) < 0) {
                    boneIds!!.put(baseOffset + i, bone.index)
                    boneWeights!!.put(baseOffset + i, it.weight)
                    break
                }
            }
        }
    }

    fun destroy() {
        MemoryUtil.memFree(vertices)
        normals?.apply(MemoryUtil::memFree)
        texCoords?.apply(MemoryUtil::memFree)
        indices?.apply(MemoryUtil::memFree)
        boneIds?.apply(MemoryUtil::memFree)
        boneWeights?.apply(MemoryUtil::memFree)
    }

    fun toGeometry(material: Material = Material.Default): Geometry {
        return Geometry(name, material, aabb).also { g ->
            g.initialize(this)
            destroy()
        }
    }

}