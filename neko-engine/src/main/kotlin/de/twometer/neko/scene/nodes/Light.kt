package de.twometer.neko.scene.nodes

import de.twometer.neko.render.Primitive
import de.twometer.neko.render.Primitives
import de.twometer.neko.scene.Color
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
            (-linear + MathF.sqrt(linear * linear - 4 * quadratic * (constant - (255.0f / 7f) * lightMax))) / (2 * quadratic)
    }

}

class DirectionalLight : Light() {

    // https://learnopengl.com/Advanced-Lighting/Shadows/Shadow-Mapping

    override fun getPrimitive(): Primitive {
        TODO("Not suitable for this type of light")
    }

}