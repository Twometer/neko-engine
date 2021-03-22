package de.twometer.neko.scene.nodes

import de.twometer.neko.render.TextureCube
import de.twometer.neko.render.Primitives
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.Material
import de.twometer.neko.scene.RenderBucket

class Sky(cubemap: TextureCube) :
    RenderableNode(
        Material("Skybox", hashMapOf(Pair(MatKey.TextureDiffuse, cubemap)), "base/skybox.nks"),
        RenderBucket.Forward
    ) {

    override fun render() {
        Primitives.skybox.render()
    }

}