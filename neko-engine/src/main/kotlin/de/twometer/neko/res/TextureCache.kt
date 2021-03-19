package de.twometer.neko.res

import de.twometer.neko.render.Texture2d
import de.twometer.neko.util.Cache

object TextureCache : Cache<String, Texture2d>() {

    override fun create(key: String): Texture2d = TextureLoader.load(key)

}