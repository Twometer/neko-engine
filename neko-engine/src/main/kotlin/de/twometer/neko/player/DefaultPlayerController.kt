package de.twometer.neko.player

import de.twometer.neko.core.NekoApp
import de.twometer.neko.core.Window
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.MathExtensions.clone
import org.lwjgl.glfw.GLFW

class DefaultPlayerController : PlayerController {

    var speed = 2.5f
    var sensitivity = 0.25f

    override fun updateCamera(window: Window, scene: Scene, deltaTime: Double) {
        if (!window.isFocused() || NekoApp.the.cursorVisible)
            return

        var speed = this.speed * deltaTime.toFloat()
        val sensitivity = this.sensitivity * deltaTime.toFloat()

        if (window.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            speed *= 4
        }
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

        val (winW, winH) = window.getSize()
        val (curX, curY) = window.getCursorPosition()

        val dx = winW / 2 - curX
        val dy = winH / 2 - curY

        scene.camera.rotation.x += dx.toFloat() * sensitivity
        scene.camera.rotation.y += dy.toFloat() * sensitivity

        window.setCursorPosition(winW / 2, winH / 2)
    }

}