package de.twometer.neko.res

import de.twometer.neko.gl.Shader
import de.twometer.neko.util.Cache

object ShaderCache : Cache<String, Shader>() {

    override fun create(key: String): Shader = Shader(ShaderLoader.loadFromFile(key))

}