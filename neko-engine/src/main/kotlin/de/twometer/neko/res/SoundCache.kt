package de.twometer.neko.res

import de.twometer.neko.audio.SoundBuffer
import de.twometer.neko.util.Cache

object SoundCache : Cache<String, SoundBuffer>() {

    override fun create(key: String): SoundBuffer = SoundLoader.load(key)

}