package de.twometer.neko.render

import de.twometer.neko.res.TextureLoader
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

}