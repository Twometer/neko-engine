package de.twometer.neko.scene

import de.twometer.neko.render.Primitive
import de.twometer.neko.render.Primitives
import de.twometer.neko.util.MathF

abstract class Light : Node() {

    var color: Color = Color.White

    var active = true

    abstract fun getPrimitive(): Primitive

}

class PointLight : Light() {

    var constant: Float = 1.0f
        set(value) {
            field = value
            recomputeRadius()
        }

    var linear: Float = 0.7f
        set(value) {
            field = value
            recomputeRadius()
        }

    var quadratic = 1.8f
        set(value) {
            field = value
            recomputeRadius()
        }

    var radius: Float = 0.0f
        private set

    init {
        recomputeRadius()
    }

    override fun getPrimitive(): Primitive = Primitives.unitSphere

    private fun recomputeRadius() {
        val lightMax = MathF.max(color.r, color.g, color.b)
        radius =
            (-linear + MathF.sqrt(linear * linear - 4 * quadratic * (constant - (256.0f / 5.0f) * lightMax))) / (2 * quadratic)
    }

}