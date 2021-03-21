package de.twometer.neko.scene

abstract class RenderableNode(val material: Material, var bucket: RenderBucket = RenderBucket.Deferred, name: String = "") : Node(name = name) {

    abstract fun render()

}