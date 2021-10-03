package de.twometer.neko.scene

import de.twometer.neko.core.NekoApp
import de.twometer.neko.util.MathF
import de.twometer.neko.util.MathF.cos
import de.twometer.neko.util.MathF.sin
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f


class Camera {

    val position = Vector3f(1f, 1f, 1f)
    val rotation = Vector2f()

    val viewMatrix = Matrix4f()
    val projectionMatrix = Matrix4f()
    private val projViewMatrix = Matrix4f()
    private val frustumPlanes = Array(6) { Vector4f() }

    var fov = MathF.toRadians(70.0f)
    var zNear = 0.1f
    var zFar = 150.0f

    lateinit var direction: Vector3f
        private set
    lateinit var right: Vector3f
        private set
    lateinit var up: Vector3f
        private set

    fun update() {
        val (width, height) = NekoApp.the.window.getSize()
        val aspect = width.toFloat() / height.toFloat()

        val yaw = rotation.x
        val pitch = rotation.y

        direction = Vector3f(
            cos(pitch) * sin(yaw),
            sin(pitch),
            cos(pitch) * cos(yaw)
        )

        right = Vector3f(
            sin(yaw - MathF.PI / 2.0f),
            0.0f,
            cos(yaw - MathF.PI / 2.0f)
        )

        up = Vector3f(right).cross(direction)

        viewMatrix.identity().lookAt(position, Vector3f(position).add(direction), up)
        projectionMatrix.identity().perspective(fov, aspect, zNear, zFar)

        projViewMatrix.set(projectionMatrix).mul(viewMatrix)
        for (i in 0..5)
            projViewMatrix.frustumPlane(i, frustumPlanes[i])
    }

    fun isInFrustum(vec: Vector3f, radius: Float): Boolean {
        return frustumPlanes.none { it.x * vec.x + it.y * vec.y + it.z * vec.z + it.w <= -radius }
    }

}