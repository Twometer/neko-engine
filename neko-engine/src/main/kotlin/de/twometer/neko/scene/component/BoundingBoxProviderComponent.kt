package de.twometer.neko.scene.component

import de.twometer.neko.scene.AABB
import de.twometer.neko.scene.nodes.Node

class BoundingBoxProviderComponent(val provider: (Node) -> AABB) : BaseComponent() {

    override fun createInstance(): BaseComponent {
        return BoundingBoxProviderComponent(provider)
    }

}