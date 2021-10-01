package de.twometer.neko.res

import de.twometer.neko.audio.SoundBuffer
import mu.KotlinLogging
import org.lwjgl.openal.AL10.*
import org.lwjgl.stb.STBVorbis.*
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.File
import java.nio.ByteBuffer
import java.nio.ShortBuffer

private val logger = KotlinLogging.logger {}

object SoundLoader {

    fun load(path: String): SoundBuffer {
        val file = AssetManager.resolve(path, AssetType.Sounds)
        logger.info { "Loading sound $path" }

        val bufferId = alGenBuffers()
        STBVorbisInfo.malloc().use { info ->
            val pcmData = readVorbis(file, info)
            val bufFormat = if (info.channels() == 1) AL_FORMAT_MONO16 else AL_FORMAT_STEREO16
            alBufferData(bufferId, bufFormat, pcmData, info.sample_rate())
            MemoryUtil.memFree(pcmData)
        }
        return SoundBuffer(bufferId)
    }

    private fun readVorbis(file: File, info: STBVorbisInfo): ShortBuffer {
        MemoryStack.stackPush().use {
            val vorbisData = readBytes(file)

            val error = it.mallocInt(1)
            val decoder = stb_vorbis_open_memory(vorbisData, error, null)

            if (decoder == MemoryUtil.NULL) {
                throw RuntimeException("Failed to open STB Vorbis decoder (Error: ${error.get(0)})")
            }

            stb_vorbis_get_info(decoder, info)
            val channels = info.channels()
            val numSamples = stb_vorbis_stream_length_in_samples(decoder)
            val pcm = MemoryUtil.memAllocShort(numSamples)

            val numSamplesRead = stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels
            pcm.limit(numSamplesRead)

            stb_vorbis_close(decoder)
            MemoryUtil.memFree(vorbisData)
            return pcm
        }
    }

    private fun readBytes(file: File): ByteBuffer {
        val data = MemoryUtil.memAlloc(file.length().toInt())
        file.inputStream().use {
            data.put(it.readAllBytes())
        }
        data.flip()
        return data
    }

}