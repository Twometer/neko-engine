package de.twometer.neko.res

import de.twometer.neko.render.Cubemap
import de.twometer.neko.render.Texture
import de.twometer.neko.util.Cache

object CubemapCache : Cache<String, Cubemap>() {

    override fun create(key: String): Cubemap = CubemapLoader.load(key)

}