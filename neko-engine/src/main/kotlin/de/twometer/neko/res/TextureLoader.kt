package de.twometer.neko.res

import de.twometer.neko.render.Texture2d
import mu.KotlinLogging
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.glGenerateMipmap
import java.nio.ByteBuffer

private val logger = KotlinLogging.logger {}

object TextureLoader {

    fun load(path: String): Texture2d {
        val filename = AssetManager.resolve(path, AssetType.Textures).absolutePath
        logger.debug { "Loading texture $path" }

        val image = ImageLoader.load(filename)
        val texture = load(image.pixels, image.width, image.height)
        return texture.also { image.destroy() }
    }

    fun load(pixels: ByteBuffer, width: Int, height: Int, mipmap: Boolean = true): Texture2d {
        val textureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, if (mipmap) GL_LINEAR_MIPMAP_LINEAR else GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        if (mipmap)
            glGenerateMipmap(GL_TEXTURE_2D)

        glBindTexture(GL_TEXTURE_2D, 0)

        return Texture2d(textureId, width, height)
    }

}