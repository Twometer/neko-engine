package de.twometer.neko.gl

import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture

data class Texture(val textureId: Int, val width: Int, val height: Int) {

    fun bind(unit: Int = 0) {
        glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(GL_TEXTURE_2D, textureId)
    }

    fun unbind() = glBindTexture(GL_TEXTURE_2D, 0)

}
