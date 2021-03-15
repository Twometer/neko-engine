package de.twometer.neko.scene

import de.twometer.neko.render.Primitive
import de.twometer.neko.render.Primitives

abstract class Light : Node() {

    var power: Float = 1.0f

    var color: Color = Color.White

    abstract fun getPrimitive(): Primitive

}

class PointLight : Light() {

    override fun getPrimitive(): Primitive = Primitives.unitSphere

}