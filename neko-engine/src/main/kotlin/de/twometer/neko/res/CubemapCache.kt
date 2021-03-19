package de.twometer.neko.res

import de.twometer.neko.render.TextureCube
import de.twometer.neko.util.Cache

object CubemapCache : Cache<String, TextureCube>() {

    override fun create(key: String): TextureCube = CubemapLoader.load(key)

}