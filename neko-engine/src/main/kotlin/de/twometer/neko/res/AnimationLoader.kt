package de.twometer.neko.res

import de.twometer.neko.scene.*
import mu.KotlinLogging
import org.joml.Quaternionf
import org.joml.Vector3f
import java.io.IOException

private val logger = KotlinLogging.logger {}

object AnimationLoader {

    fun loadAnimation(path: String): Animation {
        logger.info { "Loading animation $path" }

        var ticksPerSecond = 0.0
        var duration = 0.0
        val channels = HashMap<String, AnimationChannel>()
        var currentChannel: AnimationChannel? = null

        val lines = AssetManager.resolve(path, AssetType.Animations).readLines()
        lines.map { it.trim().split(" ") }
            .filter { it.isNotEmpty() && it.size > 1 }
            .forEach {
                when (it[0]) {
                    "a" -> {
                        ticksPerSecond = it[2].toDouble()
                        duration = it[3].toDouble()
                    }
                    "c" -> {
                        val channel = AnimationChannel(it.joinToString(" ").substring(2))
                        channels[channel.name] = channel
                        currentChannel = channel
                    }
                    "p" -> {
                        currentChannel!!.positionKeyframes.add(
                            PositionKeyframe(
                                it[1].toDouble(),
                                Vector3f(
                                    it[2].toFloat(),
                                    it[3].toFloat(),
                                    it[4].toFloat()
                                )
                            )
                        )
                    }
                    "r" -> {
                        currentChannel!!.rotationKeyframes.add(
                            RotationKeyframe(
                                it[1].toDouble(),
                                Quaternionf(
                                    it[2].toFloat(),
                                    it[3].toFloat(),
                                    it[4].toFloat(),
                                    it[5].toFloat()
                                )
                            )
                        )
                    }
                    "s" -> {
                        currentChannel!!.scaleKeyframes.add(
                            ScaleKeyframe(
                                it[1].toDouble(),
                                Vector3f(
                                    it[2].toFloat(),
                                    it[3].toFloat(),
                                    it[4].toFloat()
                                )
                            )
                        )
                    }
                }
            }

        if (channels.size == 0 || ticksPerSecond == 0.0 || duration == 0.0)
            throw IOException("Invalid animation $path")

        return Animation(ticksPerSecond, duration, channels)
    }

}