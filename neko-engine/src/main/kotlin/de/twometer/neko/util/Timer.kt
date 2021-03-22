package de.twometer.neko.util

import org.lwjgl.glfw.GLFW.glfwGetTime

class Timer(tps: Int) {

    private val delay: Double = 1.0 / tps
    private var lastReset = 0.0

    fun reset() {
        lastReset = glfwGetTime()
    }

    fun elapsed(): Boolean = glfwGetTime() - lastReset > delay

    fun getPartial(): Double = 1.0 - ((lastReset + delay - glfwGetTime()) / delay)

}