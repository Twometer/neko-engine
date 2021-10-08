package de.twometer.neko.scene.nodes

object ScanFilters {

    val MODEL = { node: Node -> node is ModelNode }
    val RENDERABLE = { node: Node -> node is RenderableNode }
    val GEOMETRY = { node: Node -> node is Geometry }
    val LIGHT = { node: Node -> node is Light }

}