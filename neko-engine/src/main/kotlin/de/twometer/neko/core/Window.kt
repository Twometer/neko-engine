package de.twometer.neko.core

import de.twometer.neko.events.*
import de.twometer.neko.res.AssetManager
import de.twometer.neko.res.AssetType
import de.twometer.neko.res.ImageLoader
import de.twometer.neko.util.Cache
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.NULL


class Window(private val config: AppConfig) {

    var handle: Long = 0
        private set

    private var cursorVisible: Boolean = true

    private val cursorCache = object : Cache<Int, Long>() {
        override fun create(key: Int): Long = glfwCreateStandardCursor(key)
    }

    fun create() {
        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit())
            error("GLFW init failed")

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, config.glMajor)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, config.glMinor)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

        handle = glfwCreateWindow(config.windowWidth, config.windowHeight, config.windowTitle, 0, 0)
        if (handle == NULL)
            error("GLFW window creation failed")

        glfwMakeContextCurrent(handle)
        glfwSwapInterval(1)

        GL.createCapabilities()

        glfwSetFramebufferSizeCallback(handle) { _, width, height -> Events.post(ResizeEvent(width, height)) }
        glfwSetWindowFocusCallback(handle) { _, focused -> Events.post(FocusEvent(focused)) }
        glfwSetCharCallback(handle) { _, codepoint -> Events.post(CharEvent(codepoint.toChar(), codepoint)) }
        glfwSetCursorPosCallback(handle) { _, x, y -> Events.post(MouseMoveEvent(x.toInt(), y.toInt())) }
        glfwSetScrollCallback(handle) { _, x, y -> Events.post(MouseWheelEvent(x.toInt(), y.toInt())) }

        glfwSetMouseButtonCallback(handle) { _, button, action, mods ->
            if (action == GLFW_RELEASE)
                Events.post(MouseClickEvent(button))
            Events.post(MouseButtonEvent(button, action, mods))
        }

        glfwSetKeyCallback(handle) { _, key, scancode, action, mods ->
            if (action == GLFW_REPEAT || action == GLFW_PRESS)
                Events.post(KeyPressEvent(key))
            Events.post(KeyEvent(key, scancode, action, mods))
        }

        if (config.windowIcon != null) {
            val image = ImageLoader.load(AssetManager.resolve(config.windowIcon, AssetType.Textures).absolutePath)
            val glfwArray = GLFWImage.malloc(1)
            val glfwImage = GLFWImage.malloc()
            glfwImage.set(image.width, image.height, image.pixels)
            glfwArray.put(0, glfwImage)
            glfwSetWindowIcon(handle, glfwArray)
        }
    }

    fun update() {
        glfwSwapBuffers(handle)
        glfwPollEvents()
    }

    fun destroy() {
        if (handle == NULL)
            return

        cursorCache.map().forEach { glfwDestroyCursor(it.value) }
        glfwFreeCallbacks(handle)
        glfwDestroyWindow(handle)
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
        GL.destroy()
    }

    fun setSize(width: Int, height: Int) = glfwSetWindowSize(handle, width, height)

    fun setTitle(title: String) = glfwSetWindowTitle(handle, title)

    fun setCursorVisible(visible: Boolean) {
        if (visible != cursorVisible) {
            glfwSetInputMode(handle, GLFW_CURSOR, if (visible) GLFW_CURSOR_NORMAL else GLFW_CURSOR_DISABLED)
            cursorVisible = visible
        }
    }

    fun setCursorPosition(x: Int, y: Int) = glfwSetCursorPos(handle, x.toDouble(), y.toDouble())

    fun setCursor(cursor: Int) = glfwSetCursor(handle, if (cursor == 0) 0 else cursorCache.get(cursor))

    fun setClipboardContent(str: String) {
        glfwSetClipboardString(handle, str)
    }

    fun getClipboardContent(): String? {
        return glfwGetClipboardString(handle)
    }

    fun getScale(): Float {
        val f = floatArrayOf(0.0f)
        glfwGetWindowContentScale(handle, f, f)
        return f[0]
    }

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

    fun isKeyDown(key: Int) =
        glfwGetKey(handle, key) == GLFW_PRESS

    fun isMouseButtonPressed(key: Int) = glfwGetMouseButton(handle, key) == GLFW_PRESS

    fun close() {
        glfwSetWindowShouldClose(handle, true)
    }

}