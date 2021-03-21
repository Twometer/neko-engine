package de.twometer.neko.scene

import org.joml.Quaternionf
import org.joml.Vector3f

/**
 * An Animation holds data describing how a rigged mesh can be animated
 */
data class Animation(
    val ticksPerSecond: Double,
    val duration: Double,
    val channels: MutableMap<String, AnimationChannel> = HashMap()
)

/**
 * An AnimationChannel holds keyframes for a single bone
 */
data class AnimationChannel(
    val name: String,
    val positionKeyframes: MutableList<PositionKeyframe> = ArrayList(),
    val rotationKeyframes: MutableList<RotationKeyframe> = ArrayList(),
    val scaleKeyframes: MutableList<ScaleKeyframe> = ArrayList()
)

open class Keyframe(val time: Double)
class PositionKeyframe(time: Double, val position: Vector3f) : Keyframe(time)
class RotationKeyframe(time: Double, val rotation: Quaternionf) : Keyframe(time)
class ScaleKeyframe(time: Double, val scale: Vector3f) : Keyframe(time)