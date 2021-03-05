package de.twometer.neko.util

import org.joml.*

object MathExtensions {

    fun Vector2f.clone(): Vector2f = Vector2f(this)
    fun Vector3f.clone(): Vector3f = Vector3f(this)
    fun Vector4f.clone(): Vector4f = Vector4f(this)
    fun Matrix3f.clone(): Matrix3f = Matrix3f(this)
    fun Matrix4f.clone(): Matrix4f = Matrix4f(this)
    fun Quaternionf.clone(): Quaternionf = Quaternionf(this)

}