package de.twometer.neko.player

import de.twometer.neko.core.Window
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.MathExtensions.clone
import org.lwjgl.glfw.GLFW

class DefaultPlayerController : PlayerController {

    override fun updateCamera(window: Window, scene: Scene) {
        val speed = 0.15f
        val sensitivity = 0.002f

        if (window.isKeyDown(GLFW.GLFW_KEY_W)) {
            scene.camera.position.add(scene.camera.direction.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW.GLFW_KEY_S)) {
            scene.camera.position.sub(scene.camera.direction.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW.GLFW_KEY_A)) {
            scene.camera.position.sub(scene.camera.right.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW.GLFW_KEY_D)) {
            scene.camera.position.add(scene.camera.right.clone().mul(speed))
        }
        if (window.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
            scene.camera.position.y += speed
        }
        if (window.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            scene.camera.position.y -= speed
        }

        val (curX, curY) = window.getCursorPosition()

        val dx = 10 - curX
        val dy = 10 - curY

        scene.camera.rotation.x += dx.toFloat() * sensitivity
        scene.camera.rotation.y += dy.toFloat() * sensitivity

        window.setCursorPosition(10, 10)
    }

}