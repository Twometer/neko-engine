package de.twometer.neko.model

open class Node(
    val transform: Transform = Transform(),
    var parent: Node? = null,
    val children: MutableList<Node> = ArrayList()
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

}