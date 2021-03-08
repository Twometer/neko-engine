package de.twometer.neko.render

import de.twometer.neko.events.Events
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.res.TextureCache
import de.twometer.neko.scene.Geometry
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.Scene
import org.greenrobot.eventbus.Subscribe
import org.lwjgl.opengl.GL30.*

class SceneRenderer(val scene: Scene) {

    private var gBuffer: Framebuffer? = null
    lateinit var lightingShader: Shader

    fun setup() {
        Events.register(this)

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        lightingShader = ShaderCache.get("base/deferred.lighting.nks")
    }

    @Subscribe
    fun onSizeChanged(event: ResizeEvent) {
        glViewport(0, 0, event.width, event.height)
        gBuffer?.destroy()
        gBuffer = Framebuffer(event.width, event.height)
            .addDepthBuffer()
            .addColorTexture(0, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)  // Positions
            .addColorTexture(1, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)  // Normals
            .addColorTexture(2, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)  // Albedo

        gBuffer!!.verify()
    }

    fun renderFrame() {
        // gBuffer!!.bind()

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

        // gBuffer!!.unbind()


    }

}