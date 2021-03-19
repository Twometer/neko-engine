package de.twometer.neko.res

import de.twometer.neko.render.TextureCube
import mu.KotlinLogging
import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R
import org.lwjgl.opengl.GL13.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X
import org.lwjgl.opengl.GL30.glGenerateMipmap

private val logger = KotlinLogging.logger {}

enum class CubemapSide {
    PosX,
    NegX,
    PosY,
    NegY,
    PosZ,
    NegZ
}

object CubemapLoader {

    private fun loadImages(path: String, fileFmt: String): List<Image> {
        val images = ArrayList<Image>()

        for (side in CubemapSide.values()) {
            val relativePath = "$path/${side.name.toLowerCase()}.$fileFmt"
            val imagePath = AssetManager.resolve(relativePath, AssetType.Textures)
            logger.debug { "Loading cubemap side $side from $relativePath" }
            images.add(ImageLoader.load(imagePath.absolutePath))
        }

        return images
    }

    fun load(path: String, fileFmt: String = "png"): TextureCube {
        logger.info { "Loading .$fileFmt cubemap from $path" }

        val textureId = glGenTextures()
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId)

        val images = loadImages(path, fileFmt)
        images.forEachIndexed { index, image ->
            glTexImage2D(
                GL_TEXTURE_CUBE_MAP_POSITIVE_X + index,
                0,
                GL_RGB,
                image.width,
                image.height,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                image.pixels
            )
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)
        glGenerateMipmap(GL_TEXTURE_CUBE_MAP)

        images.forEach { it.destroy() }
        return TextureCube(textureId)
    }

}