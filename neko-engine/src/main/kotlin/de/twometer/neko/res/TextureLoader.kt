package de.twometer.neko.res

import de.twometer.neko.render.Texture
import mu.KotlinLogging
import org.lwjgl.opengl.ARBFramebufferObject.glGenerateMipmap
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

private val logger = KotlinLogging.logger {}

object TextureLoader {


    fun load(path: String): Texture {
        val filename = AssetManager.resolve(path, AssetType.Textures).absolutePath
        logger.debug { "Loading texture $path" }

        var pixels: ByteBuffer
        var width: Int
        var height: Int

        MemoryStack.stackPush().use {
            val widthBuf = it.mallocInt(1)
            val heightBuf = it.mallocInt(1)
            val channelsBuf = it.mallocInt(1)

            pixels = stbi_load(filename, widthBuf, heightBuf, channelsBuf, 4)
                ?: error("STBI failed to load image from $path")

            width = widthBuf.get()
            height = heightBuf.get()
        }

        val textureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glGenerateMipmap(GL_TEXTURE_2D)

        glBindTexture(GL_TEXTURE_2D, 0)

        stbi_image_free(pixels)

        return Texture(textureId, width, height)
    }

}