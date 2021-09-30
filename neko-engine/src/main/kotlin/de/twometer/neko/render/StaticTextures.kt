package de.twometer.neko.render

import de.twometer.neko.res.TextureLoader
import de.twometer.neko.util.MathF
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.GL_RGBA16F
import org.lwjgl.system.MemoryStack

object StaticTextures {

    val white by lazy {
        MemoryStack.stackPush().use {
            val buf = it.malloc(4)
            repeat(4) { buf.put(255.toByte()) }
            buf.flip()
            return@lazy TextureLoader.load(buf, 1, 1, false)
        }
    }

    val noise4x4 by lazy {
        MemoryStack.stackPush().use {
            val buffer = it.mallocFloat(16 * 3)
            for (i in 0..15) {
                buffer.put(MathF.rand() * 2.0f - 1.0f)
                buffer.put(MathF.rand() * 2.0f - 1.0f)
                buffer.put(0f)
            }
            buffer.flip()

            val texture = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, texture)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 4, 4, 0, GL_RGB, GL_FLOAT, buffer)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            return@lazy Texture2d(texture, 4, 4)
        }

    }

}