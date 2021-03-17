package de.twometer.neko.util

import org.joml.Random
import org.joml.Vector2f
import org.joml.Vector3f

object MathF {
    private val random: Random = Random()

    const val PI = Math.PI.toFloat()
    const val E = Math.E.toFloat()

    fun sin(a: Float): Float {
        return kotlin.math.sin(a.toDouble()).toFloat()
    }

    fun asin(a: Float): Float {
        return kotlin.math.asin(a.toDouble()).toFloat()
    }

    fun cos(a: Float): Float {
        return kotlin.math.cos(a.toDouble()).toFloat()
    }

    fun acos(a: Float): Float {
        return kotlin.math.acos(a.toDouble()).toFloat()
    }

    fun tan(a: Float): Float {
        return kotlin.math.tan(a.toDouble()).toFloat()
    }

    fun atan(a: Float): Float {
        return kotlin.math.tan(a.toDouble()).toFloat()
    }

    fun atan2(y: Float, x: Float): Float {
        return kotlin.math.atan2(y.toDouble(), x.toDouble()).toFloat()
    }

    fun toRadians(deg: Float): Float {
        return Math.toRadians(deg.toDouble()).toFloat()
    }

    fun toDegrees(rad: Float): Float {
        return Math.toDegrees(rad.toDouble()).toFloat()
    }

    fun lerp(a: Float, b: Float, f: Float): Float {
        return a + f * (b - a)
    }

    fun lerp(a: Vector2f, b: Vector2f, f: Float): Vector2f {
        return Vector2f(
            lerp(a.x, b.x, f),
            lerp(a.y, b.y, f)
        )
    }

    fun lerp(a: Vector3f, b: Vector3f, f: Float): Vector3f {
        return Vector3f(
            lerp(a.x, b.x, f),
            lerp(a.y, b.y, f),
            lerp(a.z, b.z, f)
        )
    }

    fun rand(): Float {
        return random.nextFloat()
    }

    fun clamp(min: Float, max: Float, f: Float): Float {
        return when {
            f < min -> min
            f > max -> max
            else -> f
        }
    }

    fun min(a: Float, b: Float): Float {
        return a.coerceAtMost(b)
    }

    fun min(a: Float, b: Float, c: Float): Float {
        return a.coerceAtMost(b).coerceAtMost(c)
    }

    fun max(a: Float, b: Float): Float {
        return a.coerceAtLeast(b)
    }

    fun max(a: Float, b: Float, c: Float): Float {
        return a.coerceAtLeast(b).coerceAtLeast(c)
    }

    fun sqrt(v: Float): Float = kotlin.math.sqrt(v)

}