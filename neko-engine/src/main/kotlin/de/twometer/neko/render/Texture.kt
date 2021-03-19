package de.twometer.neko.render

import org.lwjgl.opengl.GL13.*

abstract class Texture(val textureId: Int) {

    abstract val textureTarget: Int

    fun bind(unit: Int = 0) {
        glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(textureTarget, textureId)
    }

    fun unbind() = glBindTexture(textureTarget, 0)

}