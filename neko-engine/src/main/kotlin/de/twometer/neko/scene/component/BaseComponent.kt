package de.twometer.neko.scene.component

import de.twometer.neko.scene.nodes.Node

abstract class BaseComponent(var parent: Node? = null) {

    abstract fun createInstance(): BaseComponent

}