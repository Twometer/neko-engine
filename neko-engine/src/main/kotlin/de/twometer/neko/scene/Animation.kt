package de.twometer.neko.scene

import org.joml.Matrix4f
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

/**
 * A Bone represents a single animation joint in a mesh.
 */
data class Bone(
    val name: String,
    val index: Int,
    val offsetMatrix: Matrix4f,
    val localTransform: Matrix4f = Matrix4f(),
    val weights: MutableList<BoneWeight> = ArrayList()
)

/**
 * A BoneWeight represents the influence of a single bone on a single vertex
 */
data class BoneWeight(val vertexId: Int, val weight: Float)

/**
 * A Keyframe represents the state of a single bone at an exact point in time
 */
open class Keyframe(val time: Double)

/**
 * A SkeletonNode represents a node in a hierarchy graph of bones (a skeleton)
 */
data class SkeletonNode(
    val bone: Bone?,
    val transform: Matrix4f = Matrix4f(),
    val children: MutableList<SkeletonNode> = ArrayList()
)

/**
 * A PositionKeyframe represents the position of a single bone at an exact point in tíme
 */
class PositionKeyframe(time: Double, val position: Vector3f) : Keyframe(time)

/**
 * A RotationKeyframe represents the rotation of a single bone at an exact point in tíme
 */
class RotationKeyframe(time: Double, val rotation: Quaternionf) : Keyframe(time)

/**
 * A ScaleKeyframe represents the scale of a single bone at an exact point in tíme
 */
class ScaleKeyframe(time: Double, val scale: Vector3f) : Keyframe(time)