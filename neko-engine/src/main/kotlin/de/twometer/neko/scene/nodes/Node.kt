package de.twometer.neko.scene.nodes

import de.twometer.neko.scene.Transform

open class Node(
    val transform: Transform = Transform(),
    var parent: Node? = null,
    val children: MutableList<Node> = ArrayList(),
    val name: String = ""
) {

    companion object {
        var idCounter: Int = 0
    }

    val id = idCounter++

    val compositeTransform: Transform
        get() = if (parent == null) transform else parent!!.compositeTransform * transform

    fun attachChild(child: Node) {
        child.parent = this
        children.add(child)
    }

    fun detach() {
        parent?.children?.remove(this)
        parent = null
    }

    fun scanTree(consumer: (Node) -> Unit) {
        children.forEach { it.scanTree(consumer) }
        consumer(this)
    }

}