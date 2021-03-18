package de.twometer.neko.scene

import de.twometer.neko.render.Cubemap
import de.twometer.neko.render.Primitives

class Sky(cubemap: Cubemap) :
    Renderable(
        Material("Skybox", hashMapOf(Pair(MatKey.TextureDiffuse, cubemap)), "base/skybox.nks"),
        RenderBucket.Forward
    ) {

    override fun render() {
        Primitives.skybox.render()
    }

}