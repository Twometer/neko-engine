package de.twometer.neko.model

data class Node(val id: Int, val transform: Transform = Transform(), var parent: Node? = null, val children: MutableList<Node> = ArrayList()) {

    fun attachChild(child: Node) {
        child.parent = this
        children.add(child)
    }

    fun remove() {
        parent?.children?.remove(this)
        parent = null
    }

}