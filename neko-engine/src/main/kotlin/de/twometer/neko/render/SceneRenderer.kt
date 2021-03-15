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

        // Basic OpenGL state
        glEnable(GL_DEPTH_TEST)
        glCullFace(GL_BACK)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        // Load and assign textures for lighting shader
        lightingShader = ShaderCache.get("base/deferred.lighting.nks")
        lightingShader.bind()
        lightingShader["gPosition"] = 0
        lightingShader["gNormal"] = 1
        lightingShader["gAlbedo"] = 2
        lightingShader.unbind()
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
            .verify()
    }

    fun renderFrame() {
        renderGBuffer()

        glClearColor(scene.backgroundColor.r, scene.backgroundColor.g, scene.backgroundColor.b, scene.backgroundColor.a)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        bindGBuffer()

        lightingShader.bind()
        Primitives.fullscreenQuad.draw()
        lightingShader.unbind()
    }

    private fun bindGBuffer() {
        gBuffer!!.getColorTexture(0).bind(0)
        gBuffer!!.getColorTexture(1).bind(1)
        gBuffer!!.getColorTexture(2).bind(2)
    }

    private fun renderGBuffer() {
        gBuffer!!.bind()
        glClearColor(0f, 0f, 0f, 0f)
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
        gBuffer!!.unbind()
    }

}