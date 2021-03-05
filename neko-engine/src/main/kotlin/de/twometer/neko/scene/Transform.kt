package de.twometer.neko.scene

import org.joml.Matrix4f
import org.joml.Vector3f

data class Transform(val translation: Matrix4f = Matrix4f(), val rotation: Matrix4f = Matrix4f()) {

    val matrix: Matrix4f
        get() {
            return rotation.mul(translation)
        }

    fun move(offset: Vector3f) {
        translation.translate(offset)
    }

    operator fun times(transform: Transform): Transform {
        return Transform(this.translation.mul(transform.translation), this.rotation.mul(transform.rotation))
    }

}
