package de.twometer.neko.core

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL


class Window(private val width: Int, private val height: Int, private val title: String) {

    private var handle: Long = 0

    fun create() {
        if (!glfwInit())
            error("GLFW init failed")

        handle = glfwCreateWindow(width, height, title, 0, 0)
        glfwMakeContextCurrent(handle)
        GL.createCapabilities()
    }

    fun closeRequested(): Boolean {
        return glfwWindowShouldClose(handle)
    }

    fun update() {
        glfwSwapBuffers(handle)
        glfwPollEvents()
    }

}