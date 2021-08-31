package de.twometer.neko.render

import org.lwjgl.opengl.GL11.*

/**
 * OpenGL wrapper with useful helper method
 * that only issues glXXX calls when the value
 * actually changed.
 */
object OpenGL {

    // Auxiliary keys
    private const val DEPTH_MASK = 100000000

    // State map
    private val states = HashMap<Int, Any>()

    private fun <T> trySetState(constant: Int, value: T, updateFunc: (T) -> Unit) {
        if (states[constant] == value)
            return

        updateFunc(value)
        states[constant] = value!!
    }

    fun setBoolean(constant: Int, active: Boolean) {
        trySetState(constant, active) {
            if (it)
                glEnable(constant)
            else
                glDisable(constant)
        }
    }

    fun enable(constant: Int) {
        setBoolean(constant, true)
    }

    fun disable(constant: Int) {
        setBoolean(constant, false)
    }

    fun cullFace(value: Int) {
        trySetState(GL_CULL_FACE, value, ::glCullFace)
    }

    fun depthMask(active: Boolean) {
        trySetState(DEPTH_MASK, active, ::glDepthMask)
    }

    fun depthFunc(func: Int) {
        trySetState(GL_DEPTH_FUNC, func, ::glDepthFunc)
    }

    fun resetState() {
        enable(GL_DEPTH_TEST)
        cullFace(GL_BACK)
        depthMask(true)
        depthFunc(GL_LESS)
    }

}