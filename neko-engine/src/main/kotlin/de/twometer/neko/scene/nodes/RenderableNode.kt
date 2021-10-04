package de.twometer.neko.scene.nodes

import de.twometer.neko.render.Shader
import de.twometer.neko.scene.Material
import de.twometer.neko.scene.RenderBucket

abstract class RenderableNode(
    val material: Material,
    var bucket: RenderBucket = RenderBucket.Deferred,
    name: String = ""
) : Node(name = name) {

    abstract fun render(shader: Shader)

}