package de.twometer.neko.util

import org.lwjgl.glfw.GLFW.glfwGetTime

class Timer(tps: Int) {

    private val delay: Double = 1.0 / tps
    private var lastReset = 0.0

    private var lastFrame = 0.0
    var deltaTime = 0.0
        private set

    fun reset() {
        lastReset = glfwGetTime()
    }

    fun elapsed(): Boolean = glfwGetTime() - lastReset > delay

    fun getPartial(): Double = 1.0 - ((lastReset + delay - glfwGetTime()) / delay)

    fun onFrame() {
        val now = glfwGetTime()
        deltaTime = now - lastFrame
        lastFrame = now
    }

}