package de.twometer.neko.audio

import org.joml.Vector3f
import org.lwjgl.openal.AL10.*


class SoundSource(buffer: SoundBuffer) {

    private var sourceId = OpenAL.newSource()

    init {
        // Reset AL source, since we might have gotten it from the cache
        pause()
        setLooping(false)
        setAbsolute(false)
        setGain(1.0f)
        setPitch(1.0f)
        setRollOffFactor(1.0f)
        setPosition(Vector3f())

        // Update the buffer
        alSourcei(sourceId, AL_BUFFER, buffer.bufferId)
    }

    val playing: Boolean
        get() = alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING

    fun setAbsolute(absolute: Boolean): SoundSource {
        if (absolute) {
            alSourcei(sourceId, AL_SOURCE_ABSOLUTE, AL_TRUE)
            alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_FALSE)
        } else {
            alSourcei(sourceId, AL_SOURCE_ABSOLUTE, AL_FALSE)
            alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE)
        }
        return this
    }

    fun setLooping(looping: Boolean): SoundSource {
        alSourcei(sourceId, AL_LOOPING, if (looping) AL_TRUE else AL_FALSE)
        return this
    }

    fun setGain(gain: Float): SoundSource {
        alSourcef(sourceId, AL_GAIN, gain)
        return this
    }

    fun setRollOffFactor(d: Float): SoundSource {
        alSourcef(sourceId, AL_ROLLOFF_FACTOR, d)
        return this
    }

    fun setSpeed(speed: Vector3f): SoundSource {
        alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z)
        return this
    }

    fun setPosition(position: Vector3f): SoundSource {
        alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z)
        return this
    }

    fun setPitch(pitch: Float): SoundSource {
        alSourcef(sourceId, AL_PITCH, pitch)
        return this
    }

    fun setProperty(param: Int, value: Float): SoundSource {
        alSourcef(sourceId, param, value)
        return this
    }

    fun play(): SoundSource {
        alSourcePlay(sourceId)
        return this
    }

    fun pause() {
        alSourcePause(sourceId)
    }

    fun stop() {
        alSourceStop(sourceId)
    }

}