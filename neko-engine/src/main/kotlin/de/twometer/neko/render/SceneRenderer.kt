package de.twometer.neko.render

import de.twometer.neko.res.ShaderCache
import de.twometer.neko.res.TextureCache
import de.twometer.neko.scene.Geometry
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.Scene
import org.lwjgl.opengl.GL11.*

class SceneRenderer(val scene: Scene) {

    fun renderFrame() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        scene.rootNode.scanTree { node ->
            if (node is Geometry) {
                val shader = ShaderCache.get(node.material.shader)

                if (node.material[MatKey.TwoSided] == true)
                    glDisable(GL_CULL_FACE)
                else
                    glEnable(GL_CULL_FACE)

                val tex = node.material[MatKey.TextureDiffuse]
                tex?.also { TextureCache.get(it.toString()).bind() }

                shader.bind()
                shader["viewMatrix"] = scene.camera.viewMatrix
                shader["projectionMatrix"] = scene.camera.projectionMatrix
                shader["modelMatrix"] = node.compositeTransform.matrix

                node.render()
            }
        }

    }

}