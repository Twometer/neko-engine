package de.twometer.neko.scene

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.abs

data class AABB(val min: Vector3f, val max: Vector3f) {

    val center: Vector3f
        get() = Vector3f(
            (min.x + max.x) / 2,
            (min.y + max.y) / 2,
            (min.z + max.z) / 2
        )

    val diagonal: Float
        get() = min.distance(max)

    val sizeX: Float
        get() = abs(max.x - min.x)

    val sizeY: Float
        get() = abs(max.y - min.y)

    val sizeZ: Float
        get() = abs(max.z - min.z)

    fun intersects(pt: Vector3f): Boolean =
        pt.x > min.x && pt.y > min.y && pt.z > min.z && pt.x < max.x && pt.y < max.y && pt.z < max.z

    fun addPoint(pt: Vector3f) {
        if (pt.x < min.x) min.x = pt.x
        if (pt.y < min.y) min.y = pt.y
        if (pt.z < min.z) min.z = pt.z

        if (pt.x > max.x) max.x = pt.x
        if (pt.y > max.y) max.y = pt.y
        if (pt.z > max.z) max.z = pt.z
    }

    fun transform(matrix: Matrix4f): AABB {
        return AABB(
            transformVector(matrix, min),
            transformVector(matrix, max),
        )
    }

    private fun transformVector(matrix: Matrix4f, vec: Vector3f): Vector3f {
        val vec4 = matrix.transform(Vector4f(vec, 1.0f))
        return Vector3f(vec4.x, vec4.y, vec4.z)
    }

}