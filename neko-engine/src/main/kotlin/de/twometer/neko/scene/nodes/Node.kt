package de.twometer.neko.scene.nodes

import de.twometer.neko.scene.Transform
import de.twometer.neko.scene.component.BaseComponent
import java.util.function.Predicate

open class Node(
    val name: String = "",
    var parent: Node? = null,
    val transform: Transform = Transform(),
    val children: MutableList<Node> = ArrayList(),
    val components: HashMap<Class<out BaseComponent>, BaseComponent> = HashMap(),
) {

    companion object {
        var idCounter: Int = 0
    }

    val id = idCounter++

    val compositeTransform: Transform
        get() = if (parent == null) transform else parent!!.compositeTransform * transform

    inline fun <reified T : BaseComponent> getComponent(): T? {
        val clazz = T::class.java
        val component = components[clazz]
        return if (component?.javaClass == clazz) {
            component as T
        } else {
            null
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

    fun scanTree(filter: (Node) -> Boolean, consumer: (Node) -> Unit) {
        children.forEach { it.scanTree(filter, consumer) }
        if (filter(this)) consumer(this)
    }

    fun scanTree(consumer: (Node) -> Unit) {
        children.forEach { it.scanTree(consumer) }
        consumer(this)
    }

    fun detachAll(selector: Predicate<Node>) {
        val it = children.iterator()
        while (it.hasNext()) {
            val child = it.next()
            child.detachAll(selector)
            if (selector.test(child)) {
                it.remove()
                child.parent = null
            }
        }
    }

    protected open fun createInstance(): Node {
        val node = Node(name = name)
        node.initializeFrom(this)
        return node
    }

    protected fun initializeFrom(other: Node) {
        transform.set(other.transform)
        other.components.forEach { (_, component) ->
            attachComponent(component.createInstance())
        }
        other.children.forEach {
            attachChild(it.createInstance())
        }
    }

}