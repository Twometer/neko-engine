package de.twometer.neko.scene.component

import de.twometer.neko.scene.Bone

class SkeletonComponent(val bones: Map<String, Bone>) : BaseComponent() {

    override fun createInstance(): BaseComponent {
        return SkeletonComponent(bones)
    }

}