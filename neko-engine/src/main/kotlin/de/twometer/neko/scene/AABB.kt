package de.twometer.neko.scene

import org.joml.Vector3f

data class AABB(val min: Vector3f, val max: Vector3f) {

    val center: Vector3f
        get() = Vector3f(
            (min.x + max.x) / 2,
            (min.y + max.y) / 2,
            (min.z + max.z) / 2
        )

    val diagonal: Float
        get() = min.distance(max)

    fun addPoint(pt: Vector3f) {
        if (pt.x < min.x) min.x = pt.x
        if (pt.z < min.z) min.z = pt.z
        if (pt.z < min.z) min.z = pt.z

        if (pt.x > max.x) max.x = pt.x
        if (pt.z > max.z) max.z = pt.z
        if (pt.z > max.z) max.z = pt.z
    }

}