package de.twometer.neko.render

import org.lwjgl.opengl.GL30.*

object PostRenderer {

    private val POSITIONS = floatArrayOf(-1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)
    private var vao: Int? = null

    fun fullscreenQuad() {
        if (vao == null) {
            vao = glGenVertexArrays()
            glBindVertexArray(vao!!)
            val vbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, POSITIONS, GL_STATIC_DRAW)
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
            glEnableVertexAttribArray(0)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }
        glBindVertexArray(vao!!)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glBindVertexArray(0)
    }

}