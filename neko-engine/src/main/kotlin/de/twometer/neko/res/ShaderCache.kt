package de.twometer.neko.res

import de.twometer.neko.render.Shader
import de.twometer.neko.util.Cache

object ShaderCache : Cache<String, Shader>() {

    override fun create(key: String): Shader = ShaderLoader.load(key)

}