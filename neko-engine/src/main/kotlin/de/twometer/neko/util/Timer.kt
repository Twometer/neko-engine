package de.twometer.neko.util

import org.lwjgl.glfw.GLFW.glfwGetTime

class Timer(tps: Int) {

    private val delay: Double = 1.0 / tps
    private var lastReset = 0.0
    private var lastFrame = 0.0

    val tickProgress: Double
        get() = 1.0 - ((lastReset + delay - glfwGetTime()) / delay)

    val elapsed: Boolean
        get() = glfwGetTime() - lastReset > delay

    var deltaTime = 0.0
        private set

    var fps = 0.0
        private set

    fun reset() {
        lastReset = glfwGetTime()
    }

    fun onFrame() {
        val now = glfwGetTime()
        deltaTime = now - lastFrame
        fps = 1.0 / deltaTime
        lastFrame = now
    }

}