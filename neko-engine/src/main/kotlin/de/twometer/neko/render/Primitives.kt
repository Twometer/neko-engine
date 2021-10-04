package de.twometer.neko.render

import de.twometer.neko.util.MathF
import org.joml.Vector3f
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL31.glDrawArraysInstanced

/**
 * Primitives are used as "proxy geometry" in the
 *  deferred renderer and the post-processing renderer.
 * They only have raw vertex information, and should not
 *  be used for any other purpose.
 */
object Primitives {

    val unitQuad: Primitive by lazy {
        Primitive(floatArrayOf(-1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f), GL_TRIANGLE_STRIP, 2)
    }

    val unitCube: Primitive by lazy {
        Primitive(
            floatArrayOf(
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,

                -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,

                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,

                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,

                -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, -1.0f,

                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f
            ), GL_TRIANGLES, 3
        )
    }

    val unitSphere: Primitive by lazy {
        fun sphere(u: Float, v: Float): Vector3f =
            Vector3f(MathF.cos(u) * MathF.sin(v), MathF.cos(v), MathF.sin(u) * MathF.sin(v))

        val vertices = ArrayList<Float>()

        fun addVertex(a: Vector3f) {
            vertices.add(a.x)
            vertices.add(a.y)
            vertices.add(a.z)
        }

        fun addTriangle(a: Vector3f, b: Vector3f, c: Vector3f) {
            addVertex(a)
            addVertex(b)
            addVertex(c)
        }

        val resolution = 20
        val startU = 0F
        val startV = 0F
        val endU = MathF.PI * 2F
        val endV = MathF.PI
        val stepU = (endU - startU) / resolution.toFloat()
        val stepV = (endV - startV) / resolution.toFloat()

        for (i in 0 until resolution) {
            for (j in 0 until resolution) {
                val u = i * stepU + startU
                val v = j * stepV + startV
                val un = if (i + 1 == resolution) endU else (i + 1) * stepU + startU
                val vn = if (j + 1 == resolution) endV else (j + 1) * stepV + startV

                val p0 = sphere(u, v)
                val p1 = sphere(u, vn)
                val p2 = sphere(un, v)
                val p3 = sphere(un, vn)

                addTriangle(p0, p2, p1)
                addTriangle(p3, p1, p2)
            }
        }

        Primitive(vertices.toFloatArray(), GL_TRIANGLES, 3)
    }

}

class Primitive(private val vertices: FloatArray, private val type: Int, private val dimensions: Int) {
    private val vao: Int = glGenVertexArrays()

    private val vertexCount: Int
        get() = vertices.size / dimensions

    init {
        glBindVertexArray(vao)
        val vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
        glVertexAttribPointer(0, dimensions, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    fun render() {
        glBindVertexArray(vao)
        glDrawArrays(type, 0, vertexCount)
    }

    fun renderInstanced(amount: Int) {
        glBindVertexArray(vao)
        glDrawArraysInstanced(type, 0, vertexCount, amount)
    }

}