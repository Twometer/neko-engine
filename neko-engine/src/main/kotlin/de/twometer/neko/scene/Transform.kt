package de.twometer.neko.scene

import de.twometer.neko.util.MathExtensions.clone
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

data class Transform(
    val translation: Vector3f = Vector3f(),
    val rotation: Quaternionf = Quaternionf(),
    val scale: Vector3f = Vector3f(1f, 1f, 1f)
) {

    companion object {
        fun fromMatrix(mat: Matrix4f): Transform {
            return Transform(
                mat.getTranslation(Vector3f()),
                mat.getUnnormalizedRotation(Quaternionf()),
                mat.getScale(Vector3f())
            )
        }
    }

    val matrix: Matrix4f
        get() {
            return Matrix4f().translate(translation).rotate(rotation).scale(scale)
        }

    fun reset() {
        translation.set(0f, 0f, 0f)
        rotation.set(0f, 0f, 0f, 1f)
        scale.set(1f, 1f, 1f)
    }

    fun set(other: Transform) {
        translation.set(other.translation)
        rotation.set(other.rotation)
        scale.set(other.scale)
    }

    operator fun times(transform: Transform): Transform {
        return Transform(
            this.translation.clone().add(transform.translation),
            this.rotation.clone().mul(transform.rotation),
            this.scale.clone().mul(transform.scale)
        )
    }

}
