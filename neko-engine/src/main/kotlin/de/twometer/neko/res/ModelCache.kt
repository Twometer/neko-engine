package de.twometer.neko.res

import de.twometer.neko.scene.nodes.ModelNode
import de.twometer.neko.util.Cache

object ModelCache : Cache<String, ModelNode>() {

    override fun create(key: String): ModelNode = ModelLoader.load(key)

}