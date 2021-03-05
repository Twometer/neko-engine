package de.twometer.neko.scene

import de.twometer.neko.util.MathExtensions.clone
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

data class Transform(val translation: Vector3f = Vector3f(), val rotation: Quaternionf = Quaternionf()) {

    val matrix: Matrix4f
        get() {
            return Matrix4f().translate(translation).rotate(rotation)
        }

    operator fun times(transform: Transform): Transform {
        return Transform(this.translation.clone().add(transform.translation), this.rotation.clone().mul(transform.rotation))
    }

}
