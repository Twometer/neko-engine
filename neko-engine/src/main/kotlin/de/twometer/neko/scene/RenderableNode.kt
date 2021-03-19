package de.twometer.neko.scene

abstract class RenderableNode(val material: Material, var bucket: RenderBucket = RenderBucket.Deferred) :

    Node() {

    abstract fun render()

}