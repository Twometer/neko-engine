package de.twometer.neko.render

import org.lwjgl.opengl.GL11.GL_TEXTURE_2D

class Texture2d(textureId: Int, val width: Int, val height: Int) : Texture(textureId) {
    override val textureTarget: Int
        get() = GL_TEXTURE_2D
}
