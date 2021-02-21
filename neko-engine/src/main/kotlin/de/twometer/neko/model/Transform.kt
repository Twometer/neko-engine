package de.twometer.neko.model

import org.joml.Matrix4f
import org.joml.Vector3f

data class Transform(val translation: Matrix4f = Matrix4f(), val rotation: Matrix4f = Matrix4f()) {

    fun move(offset: Vector3f) {
        translation.translate(offset)
    }

    fun getMatrix(): Matrix4f? = translation.mul(rotation)

}
