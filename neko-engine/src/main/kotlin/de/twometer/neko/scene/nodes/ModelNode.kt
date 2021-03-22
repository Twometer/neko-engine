package de.twometer.neko.scene.nodes

import de.twometer.neko.scene.Animation

class ModelNode(name: String, val animations: MutableList<Animation> = ArrayList()) : Node(name = name) {

    fun playAnimation(anim: Animation) {
        children.forEach {
            if (it is Geometry && it.skeletonRoot != null)
                it.animator?.play(anim)
        }
    }

}