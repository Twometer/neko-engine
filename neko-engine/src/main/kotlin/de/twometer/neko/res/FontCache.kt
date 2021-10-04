package de.twometer.neko.res

import de.twometer.neko.font.FontFace
import de.twometer.neko.util.Cache

object FontCache : Cache<String, FontFace>() {

    override fun create(key: String): FontFace = FontLoader.load(key)
    
}