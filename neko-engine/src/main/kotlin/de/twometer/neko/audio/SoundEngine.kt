package de.twometer.neko.audio

import de.twometer.neko.res.SoundCache
import org.joml.Vector3f

object SoundEngine {

    fun newSource(name: String): SoundSource = SoundSource(SoundCache.get(name))

    fun play(name: String): SoundSource = newSource(name).play()

    fun playAt(name: String, position: Vector3f): SoundSource =
        newSource(name)
            .setAbsolute(true)
            .setPosition(position)
            .play()

}