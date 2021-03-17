package de.twometer.neko.res

import de.twometer.neko.render.Texture
import mu.KotlinLogging
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.glGenerateMipmap

private val logger = KotlinLogging.logger {}

object TextureLoader {

    fun load(path: String): Texture {
        val filename = AssetManager.resolve(path, AssetType.Textures).absolutePath
        logger.debug { "Loading texture $path" }

        val image = ImageLoader.load(filename)
        val textureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.pixels)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glGenerateMipmap(GL_TEXTURE_2D)

        glBindTexture(GL_TEXTURE_2D, 0)

        image.destroy()

        return Texture(textureId, image.width, image.height)
    }

}