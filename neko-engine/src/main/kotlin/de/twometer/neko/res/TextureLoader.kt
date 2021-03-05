package de.twometer.neko.res

import de.twometer.neko.gl.Texture
import mu.KotlinLogging
import javax.imageio.ImageIO
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.ARBFramebufferObject.glGenerateMipmap
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.ByteBuffer

private val logger = KotlinLogging.logger {}
object TextureLoader {

    private fun loadPixels(image: BufferedImage): ByteBuffer {
        val buffer = BufferUtils.createByteBuffer(image.width * image.height * 4)
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val color = Color(image.getRGB(x, y), true)
                buffer.put(color.red.toByte())
                buffer.put(color.green.toByte())
                buffer.put(color.blue.toByte())
                buffer.put(color.alpha.toByte())
            }
        }
        buffer.flip()

        return buffer
    }

    fun load(path: String): Texture {
        val image = ImageIO.read(AssetManager.resolve(path, AssetType.Textures))
        val pixels = loadPixels(image)

        logger.debug { "Loading texture $path" }

        val textureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glGenerateMipmap(GL_TEXTURE_2D)

        glBindTexture(GL_TEXTURE_2D, 0)
        return Texture(textureId, image.width, image.height)
    }

}