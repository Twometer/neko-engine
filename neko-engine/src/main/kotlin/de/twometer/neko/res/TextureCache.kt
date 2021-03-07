package de.twometer.neko.res

import de.twometer.neko.render.Texture
import de.twometer.neko.util.Cache

object TextureCache : Cache<String, Texture>() {

    override fun create(key: String): Texture = TextureLoader.load(key)

}