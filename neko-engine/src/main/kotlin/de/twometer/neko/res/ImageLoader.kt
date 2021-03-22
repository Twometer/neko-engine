package de.twometer.neko.res

import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

data class Image(val width: Int, val height: Int, val pixels: ByteBuffer) {
    fun destroy() = stbi_image_free(pixels)
}

object ImageLoader {

    fun load(path: String): Image {
        var pixels: ByteBuffer
        var width: Int
        var height: Int

        MemoryStack.stackPush().use {
            val widthBuf = it.mallocInt(1)
            val heightBuf = it.mallocInt(1)
            val channelsBuf = it.mallocInt(1)

            pixels = STBImage.stbi_load(path, widthBuf, heightBuf, channelsBuf, 4)
                ?: error("STB failed to load image from $path")

            width = widthBuf.get()
            height = heightBuf.get()
        }

        return Image(width, height, pixels)
    }

}