package de.twometer.neko.render

import de.twometer.neko.core.Window
import de.twometer.neko.events.Events
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.res.TextureCache
import de.twometer.neko.scene.Geometry
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.PointLight
import de.twometer.neko.scene.Scene
import de.twometer.neko.util.MathExtensions.clone
import org.greenrobot.eventbus.Subscribe
import org.joml.Matrix4f
import org.joml.Vector2f
import org.lwjgl.opengl.GL30.*

class SceneRenderer(val scene: Scene, val window: Window) {

    private var gBuffer: Framebuffer? = null
    lateinit var blinnShader: Shader
    lateinit var ambientShader: Shader

    fun setup() {
        Events.register(this)

        // Basic OpenGL state
        glEnable(GL_DEPTH_TEST)
        glCullFace(GL_BACK)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        // Init shader for the Blinn-Phong Lighting Model
        blinnShader = ShaderCache.get("base/lighting.blinn.nks")
        blinnShader.bind()
        blinnShader["gPosition"] = 0
        blinnShader["gNormal"] = 1
        blinnShader["gAlbedo"] = 2
        blinnShader.unbind()

        // Init shader for the ambient lighting
        ambientShader = ShaderCache.get("base/lighting.ambient.nks")
        ambientShader.bind()
        ambientShader["gAlbedo"] = 2
        ambientShader.unbind()
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
        // Render scene to GBuffer
        renderGBuffer()

        // Transfer to main buffer using deferred shading
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glDisable(GL_DEPTH_TEST)

        bindGBuffer()

        // Ambient lighting step
        ambientShader.bind()
        ambientShader["ambientStrength"] = scene.ambientStrength
        ambientShader["backgroundColor"] = scene.backgroundColor
        Primitives.fullscreenQuad.draw()
        ambientShader.unbind()

        // Blinn-Phong step (point lights)
        val (width, height) = window.getSize()
        val screenSize = Vector2f(width.toFloat(), height.toFloat())

        glBlendFunc(GL_ONE, GL_ONE)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_FRONT)

        blinnShader.bind()
        blinnShader["viewMatrix"] = scene.camera.viewMatrix
        blinnShader["projectionMatrix"] = scene.camera.projectionMatrix
        blinnShader["screenSize"] = screenSize

        // TODO: Implement full blinn-phong shading, locations, light radius etc.
        scene.rootNode.scanTree {
            if (it is PointLight) {
                val matrix = it.transform.matrix.clone()
                matrix.scale(it.radius)

                blinnShader["modelMatrix"] = matrix
                Primitives.unitSphere.draw()
                blinnShader.unbind()
            }
        }

        // Restore GL state
        glEnable(GL_DEPTH_TEST)
        glDisable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
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