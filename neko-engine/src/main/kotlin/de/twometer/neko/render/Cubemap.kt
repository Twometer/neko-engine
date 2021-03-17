package de.twometer.neko.render

import org.lwjgl.opengl.GL13.*

data class Cubemap(val textureId: Int) {

    fun bind(unit: Int = 0) {
        glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId)
    }

    fun unbind() = glBindTexture(GL_TEXTURE_CUBE_MAP, 0)

}