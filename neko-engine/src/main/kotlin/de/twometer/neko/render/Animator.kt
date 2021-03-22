package de.twometer.neko.render

import de.twometer.neko.scene.Animation
import de.twometer.neko.scene.AnimationChannel
import de.twometer.neko.scene.Keyframe
import de.twometer.neko.scene.SkeletonNode
import de.twometer.neko.util.MathExtensions.clone
import de.twometer.neko.util.MathF
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class Animator(private val skeletonNode: SkeletonNode) {

    private var time: Double = 0.0
    private var animation: Animation? = null
    private val boneMatrices: Array<Matrix4f> = Array(128) { Matrix4f() }

    fun play(animation: Animation) {
        this.animation = animation
    }

    fun update(deltaTime: Double) {
        if (animation == null)
            return

        val animation = this.animation!!

        // Increase animation timestep
        time += animation.ticksPerSecond * deltaTime
        time %= animation.duration

        // Animate bones
        calcBoneTransforms(skeletonNode, Matrix4f())
    }

    fun loadMatrices(target: Shader) {
        for (i in 0..127)
            target["boneMatrices[$i]"] = boneMatrices[i]
    }

    private fun calcBoneTransforms(node: SkeletonNode, parentTransform: Matrix4f) {
        var transform = node.transform
        val bone = node.bone
        val channel = animation?.channels?.get(node.name)

        if (channel != null) {
            transform = computeLocalTransform(channel)
        }

        val globalTransform = parentTransform.clone().mul(transform)
        bone?.apply {
            boneMatrices[index] = globalTransform.clone().mul(offsetMatrix)
        }

        node.children.forEach { calcBoneTransforms(it, globalTransform) }
    }

    private fun calcProgress(prevKeyframe: Double, nextKeyframe: Double): Double {
        val elapsedTime = time - prevKeyframe
        val totalTime = nextKeyframe - prevKeyframe
        return elapsedTime / totalTime
    }

    private fun findKeyframeIdx(frames: List<Keyframe>): Int {
        for (i in 0..(frames.size - 2)) {
            if (time < frames[i + 1].time)
                return i
        }
        error("Failed to find keyframe at time $time")
    }

    private fun computeLocalTransform(channel: AnimationChannel): Matrix4f {
        fun interpolatePosition(): Vector3f {
            if (channel.positionKeyframes.size == 1)
                return channel.positionKeyframes[0].position

            val keyframeIdx = findKeyframeIdx(channel.positionKeyframes)
            val srcKeyframe = channel.positionKeyframes[keyframeIdx]
            val dstKeyframe = channel.positionKeyframes[keyframeIdx + 1]
            val progress = calcProgress(srcKeyframe.time, dstKeyframe.time).toFloat()
            return MathF.lerp(srcKeyframe.position, dstKeyframe.position, progress)
        }

        fun interpolateRotation(): Quaternionf {
            if (channel.rotationKeyframes.size == 1)
                return channel.rotationKeyframes[0].rotation

            val keyframeIdx = findKeyframeIdx(channel.rotationKeyframes)
            val srcKeyframe = channel.rotationKeyframes[keyframeIdx]
            val dstKeyframe = channel.rotationKeyframes[keyframeIdx + 1]
            val progress = calcProgress(srcKeyframe.time, dstKeyframe.time).toFloat()
            return srcKeyframe.rotation.clone().slerp(dstKeyframe.rotation, progress)
        }

        fun interpolateScale(): Vector3f {
            if (channel.scaleKeyframes.size == 1)
                return channel.scaleKeyframes[0].scale

            val keyframeIdx = findKeyframeIdx(channel.scaleKeyframes)
            val srcKeyframe = channel.scaleKeyframes[keyframeIdx]
            val dstKeyframe = channel.scaleKeyframes[keyframeIdx + 1]
            val progress = calcProgress(srcKeyframe.time, dstKeyframe.time).toFloat()
            return MathF.lerp(srcKeyframe.scale, dstKeyframe.scale, progress)
        }

        return Matrix4f()
            .translate(interpolatePosition())
            .rotate(interpolateRotation())
            .scale(interpolateScale())
    }

}