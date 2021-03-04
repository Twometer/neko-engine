package de.twometer.neko.scene

import de.twometer.neko.core.NekoApp
import de.twometer.neko.util.MathF
import de.twometer.neko.util.MathF.cos
import de.twometer.neko.util.MathF.sin
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f


class Camera {

    val position = Vector3f(2f, 2f, 2f)

    //val position = Vector3f()
    val rotation = Vector2f()

    val viewMatrix = Matrix4f()
    val projectionMatrix = Matrix4f()

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
        val (width, height) = NekoApp.the?.window?.getSize()!!
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

        viewMatrix.identity().lookAt(position, Vector3f(0f, 0f, 0f), up)
        //viewMatrix.lookAt(position, position.add(direction), up)

        projectionMatrix.identity().perspective(fov, aspect, zNear, zFar)
    }

}