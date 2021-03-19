package de.twometer.neko.scene

import de.twometer.neko.render.TextureCube
import de.twometer.neko.render.Primitives

class Sky(cubemap: TextureCube) :
    RenderableNode(
        Material("Skybox", hashMapOf(Pair(MatKey.TextureDiffuse, cubemap)), "base/skybox.nks"),
        RenderBucket.Forward
    ) {

    override fun render() {
        Primitives.skybox.render()
    }

}