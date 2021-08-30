package de.twometer.neko.scene.nodes

import de.twometer.neko.scene.Transform
import de.twometer.neko.scene.component.BaseComponent

open class Node(
    val name: String = "",
    var parent: Node? = null,
    val transform: Transform = Transform(),
    val children: MutableList<Node> = ArrayList(),
    private val components: HashMap<Class<out BaseComponent>, BaseComponent> = HashMap(),
) {

    companion object {
        var idCounter: Int = 0
    }

    val id = idCounter++

    val compositeTransform: Transform
        get() = if (parent == null) transform else parent!!.compositeTransform * transform

    fun <T : BaseComponent> getComponent(clazz: Class<T>): T {
        val component = components[clazz]
        if (component != null && component.javaClass == clazz) {
            return component as T
        } else {
            throw Exception("Component of type ${clazz.name} is not registered on ${javaClass.name} (${name})")
        }
    }

    fun attachComponent(component: BaseComponent) {
        if (components.containsKey(component.javaClass)) {
            throw Exception("Component of type ${component.javaClass.name} registered twice on ${this.javaClass.name} (${name})")
        }
        components[component.javaClass] = component
        component.parent = this
    }

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