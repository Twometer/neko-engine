package de.twometer.neko.audio

import de.twometer.neko.scene.Camera
import mu.KotlinLogging
import org.joml.Vector3f
import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.openal.ALC11.ALC_MONO_SOURCES
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer

private val logger = KotlinLogging.logger {}

object OpenAL {

    private var device: Long = 0
    private var maxNumSources: Int = 0
    private val allocatedSources = ArrayList<Int>()

    private fun createDevice() {
        device = alcOpenDevice(null as ByteBuffer?)
        if (device == MemoryUtil.NULL) {
            throw IllegalStateException("Failed to open sound device")
        }
    }

    private fun createContext() {
        val context = alcCreateContext(device, null as IntBuffer?)
        if (context == MemoryUtil.NULL) {
            throw IllegalStateException("Failed t ocreate OpenAL context")
        }
        alcMakeContextCurrent(context)
    }

    private fun createCapabilities() {
        val alcCapabilities = ALC.createCapabilities(device)
        AL.createCapabilities(alcCapabilities)
    }

    fun open() {
        logger.info { "Loading OpenAL..." }
        createDevice()
        createContext()
        createCapabilities()
        maxNumSources = alcGetInteger(device, ALC_MONO_SOURCES)
        logger.info { "OpenAL loaded. Maximum number of sound sources: $maxNumSources" }
    }

    fun updateListener(camera: Camera) {
        alListener3f(AL_POSITION, camera.position.x, camera.position.y, camera.position.z)

        val at = camera.viewMatrix.positiveZ(Vector3f()).negate()
        val up = camera.viewMatrix.positiveY(Vector3f())
        val data = floatArrayOf(at.x, at.y, at.z, up.x, up.y, up.z)
        alListenerfv(AL_ORIENTATION, data)
    }

    fun newSource(): Int {
        val nextFreeSource = allocatedSources.firstOrNull { alGetSourcei(it, AL_SOURCE_STATE) == AL_STOPPED }
        if (nextFreeSource != null)
            return nextFreeSource

        if (allocatedSources.size >= maxNumSources)
            throw IllegalStateException("Maximum number of concurrent sound sources reached")

        val src = alGenSources()
        allocatedSources.add(src)
        return src
    }

    fun close() {
        alcCloseDevice(device)
        ALC.destroy()
    }

}