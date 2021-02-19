package de.twometer.neko.core

import de.twometer.neko.events.Events
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.util.Cache
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.GL_TRUE


class Window(private val config: AppConfig) {

    private var handle: Long = 0

    private val cursorCache = object : Cache<Int, Long>() {
        override fun create(key: Int): Long = glfwCreateStandardCursor(key)
    }

    fun create() {
        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit())
            error("GLFW init failed")

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, config.glMajor)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, config.glMinor)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

        handle = glfwCreateWindow(config.windowWidth, config.windowHeight, config.windowTitle, 0, 0)
        glfwMakeContextCurrent(handle)
        GL.createCapabilities()

        glfwSetFramebufferSizeCallback(handle) { _, width, height -> Events.post(ResizeEvent(width, height)) }
    }

    fun update() {
        glfwSwapBuffers(handle)
        glfwPollEvents()
    }

    fun destroy() {
        cursorCache.map().forEach { glfwDestroyCursor(it.value) }
        glfwFreeCallbacks(handle)
        glfwDestroyWindow(handle)
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    fun setSize(width: Int, height: Int) = glfwSetWindowSize(handle, width, height)

    fun setTitle(title: String) = glfwSetWindowTitle(handle, title)

    fun setCursorEnabled(enabled: Boolean) =
        glfwSetInputMode(handle, GLFW_CURSOR, if (enabled) GLFW_CURSOR_NORMAL else GLFW_CURSOR_DISABLED)

    fun setCursorPosition(x: Int, y: Int) = glfwSetCursorPos(handle, x.toDouble(), y.toDouble())

    fun setCursor(cursor: Int) = glfwSetCursor(handle, cursorCache.get(cursor))

    fun getSize(): Pair<Int, Int> {
        val w = intArrayOf(0)
        val h = intArrayOf(0)
        glfwGetFramebufferSize(handle, w, h)
        return Pair(w[0], h[0])
    }

    fun isCursorEnabled() = glfwGetInputMode(handle, GLFW_CURSOR) == GLFW_CURSOR_NORMAL

    fun getCursorPosition(): Pair<Double, Double> {
        val x = doubleArrayOf(0.0)
        val y = doubleArrayOf(0.0)
        glfwGetCursorPos(handle, x, y)
        return Pair(x[0], y[0])
    }

    fun isFocused() = glfwGetWindowAttrib(handle, GLFW_FOCUSED) == GLFW_TRUE

    fun isCloseRequested(): Boolean = glfwWindowShouldClose(handle)
}