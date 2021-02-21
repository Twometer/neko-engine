package de.twometer.neko.model

import de.twometer.neko.core.NekoApp
import de.twometer.neko.util.MathF
import de.twometer.neko.util.MathF.cos
import de.twometer.neko.util.MathF.sin
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f


class Camera {

    val position = Vector3f()
    val rotation = Vector2f()

    val viewMatrix = Matrix4f()
    val projectionMatrix = Matrix4f()

    var fov = MathF.toRadians(70.0f)
    var zNear = 0.1f
    var zFar = 150.0f

    fun update() {
        val (width, height) = NekoApp.the?.window?.getSize()!!
        val aspect = width.toFloat() / height.toFloat()

        val yaw = rotation.x
        val pitch = rotation.y

        val direction = Vector3f(
            cos(pitch) * sin(yaw),
            sin(pitch),
            cos(pitch) * cos(yaw)
        )

        val right = Vector3f(
            sin(yaw - MathF.PI / 2.0f),
            0.0f,
            cos(yaw - MathF.PI / 2.0f)
        )

        val up = Vector3f(right).cross(direction)

        viewMatrix.lookAt(position, direction.add(position), up)
        projectionMatrix.perspective(fov, aspect, zNear, zFar)
    }

}