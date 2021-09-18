package de.twometer.neko.render

import org.lwjgl.opengl.GL13.GL_TEXTURE0

abstract class Texture(val textureId: Int) {

    abstract val textureTarget: Int

    fun bind(unit: Int = 0) {
        OpenGL.activeTexture(GL_TEXTURE0 + unit)
        OpenGL.bindTexture(textureTarget, textureId)
    }

    fun unbind() = OpenGL.bindTexture(textureTarget, 0)

}