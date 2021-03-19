package de.twometer.neko.render

import org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP

class TextureCube(textureId: Int) : Texture(textureId) {
    override val textureTarget: Int
        get() = GL_TEXTURE_CUBE_MAP
}