package de.twometer.neko.scene.nodes

import de.twometer.neko.render.Animator
import de.twometer.neko.scene.Animation

class ModelNode(name: String, val animations: MutableList<Animation> = ArrayList()) : Node(name = name) {

    fun playAnimation(anim: Animation) {
        scanTree {
            it.getComponent<Animator>()?.play(anim)
        }
    }

    public override fun createInstance(): ModelNode {
        val modelNode = ModelNode(this.name, animations)
        modelNode.initializeFrom(this)
        return modelNode
    }
}