package de.twometer.neko.render

import de.twometer.neko.res.TextureLoader
import de.twometer.neko.util.MathF
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.GL_RGBA16F
import org.lwjgl.system.MemoryStack

object StaticTextures {

    val white by lazy { genSolidColorTexture(255, 255, 255, 255) }

    val black by lazy { genSolidColorTexture(0, 0, 0, 255) }

    val emptyNormalMap by lazy { genSolidColorTexture(128, 128, 255, 255) }

    val noise3x3 by lazy { genNoiseTexture(3) }

    val noise4x4 by lazy { genNoiseTexture(4) }

    val noise5x5 by lazy { genNoiseTexture(5) }

    private fun genSolidColorTexture(r: Int, g: Int, b: Int, a: Int): Texture2d {
        MemoryStack.stackPush().use {
            val buf = it.malloc(4)
            buf.put(r.toByte())
            buf.put(g.toByte())
            buf.put(b.toByte())
            buf.put(a.toByte())
            buf.flip()
            return TextureLoader.load(buf, 1, 1, false)
        }
    }

    private fun genNoiseTexture(size: Int): Texture2d {
        MemoryStack.stackPush().use {
            val buffer = it.mallocFloat(size * size * 3)
            for (i in 0 until (size * size)) {
                buffer.put(MathF.rand() * 2.0f - 1.0f)
                buffer.put(MathF.rand() * 2.0f - 1.0f)
                buffer.put(0f)
            }
            buffer.flip()

            val texture = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, texture)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, size, size, 0, GL_RGB, GL_FLOAT, buffer)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            return Texture2d(texture, size, size)
        }
    }

}