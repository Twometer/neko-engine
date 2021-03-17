package de.twometer.neko.render

import de.twometer.neko.core.Window
import de.twometer.neko.events.Events
import de.twometer.neko.events.RenderDeferredEvent
import de.twometer.neko.events.RenderForwardEvent
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.res.TextureCache
import de.twometer.neko.scene.*
import org.greenrobot.eventbus.Subscribe
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector2f
import org.lwjgl.opengl.GL30.*

class SceneRenderer(val scene: Scene, val window: Window) {

    private var gBuffer: Framebuffer? = null
    lateinit var blinnShader: Shader
    lateinit var ambientShader: Shader
    lateinit var skyboxShader: Shader

    fun setup() {
        Events.register(this)

        // Basic OpenGL state
        glEnable(GL_DEPTH_TEST)
        glCullFace(GL_BACK)

        // Shaders
        blinnShader = ShaderCache.get("base/lighting.blinn.nks")
        ambientShader = ShaderCache.get("base/lighting.ambient.nks")
        skyboxShader = ShaderCache.get("base/skybox.nks")
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
        glDepthMask(false)

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

        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_FRONT)

        blinnShader.bind()
        blinnShader["viewMatrix"] = scene.camera.viewMatrix
        blinnShader["projectionMatrix"] = scene.camera.projectionMatrix
        blinnShader["screenSize"] = screenSize
        blinnShader["cameraPos"] = scene.camera.position

        scene.rootNode.scanTree {
            if (it is PointLight && it.active) {
                blinnShader["modelMatrix"] = it.compositeTransform.matrix.scale(it.radius)
                blinnShader["light.position"] = it.compositeTransform.translation
                blinnShader["light.color"] = it.color
                blinnShader["light.constant"] = it.constant
                blinnShader["light.linear"] = it.linear
                blinnShader["light.quadratic"] = it.quadratic

                Primitives.unitSphere.draw()
            }
        }

        blinnShader.unbind()

        // Copy depth buffer from GBuffer to main FBO
        glDepthMask(true)
        gBuffer!!.blit(GL_DEPTH_BUFFER_BIT)

        // Prepare GL state for forward rendering
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glCullFace(GL_BACK)
        glEnable(GL_DEPTH_TEST)
        skyboxShader.bind()
        skyboxShader["viewMatrix"] = Matrix4f(Matrix3f(scene.camera.viewMatrix))
        skyboxShader["projectionMatrix"] = scene.camera.projectionMatrix
        glDepthFunc(GL_LEQUAL)
        glDepthMask(false)
        scene.rootNode.scanTree {
            if (it is Sky) {
                skyboxShader["modelMatrix"] = it.compositeTransform.matrix
                it.cubemap.bind()
                Primitives.skybox.draw()
            }
        }
        glDepthFunc(GL_LESS)
        glDepthMask(true)
        skyboxShader.unbind()

        Events.post(RenderForwardEvent())
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

        glEnable(GL_DEPTH_TEST)
        glCullFace(GL_BACK)
        glDisable(GL_BLEND)

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
                shader["specular"] = (node.material[MatKey.ColorSpecular] as? Color ?: Color.White).r
                shader["shininess"] = node.material[MatKey.Shininess] as? Float ?: 4.0f

                node.render()
            }
        }

        Events.post(RenderDeferredEvent())

        gBuffer!!.unbind()
    }

}