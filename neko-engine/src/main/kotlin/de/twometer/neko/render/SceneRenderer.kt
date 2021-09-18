package de.twometer.neko.render

import de.twometer.neko.core.NekoApp
import de.twometer.neko.events.Events
import de.twometer.neko.events.RenderDeferredEvent
import de.twometer.neko.events.RenderForwardEvent
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.res.TextureCache
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.MatKey
import de.twometer.neko.scene.RenderBucket
import de.twometer.neko.scene.Scene
import de.twometer.neko.scene.nodes.PointLight
import de.twometer.neko.scene.nodes.RenderableNode
import org.greenrobot.eventbus.Subscribe
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.lwjgl.opengl.GL30.*

class SceneRenderer(val scene: Scene) {

    private var lastTime: Double = 0.0
    private lateinit var effectsRenderer: EffectsRenderer
    private lateinit var gBuffer: FramebufferRef
    private lateinit var renderbuffer: FramebufferRef
    private lateinit var blinnShader: Shader
    private lateinit var ambientShader: Shader

    fun setup() {
        Events.register(this)

        // Shaders
        blinnShader = ShaderCache.get("base/lighting.blinn.nks")
        ambientShader = ShaderCache.get("base/lighting.ambient.nks")

        // Framebuffers
        gBuffer = FboManager.request({
            it.addDepthBuffer()
                .addColorTexture(0, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)  // Positions
                .addColorTexture(1, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)  // Normals
                .addColorTexture(2, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)  // Albedo
        })

        renderbuffer = FboManager.request({
            it.addDepthBuffer()
                .addColorTexture(0, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)
        })

        effectsRenderer = EffectsRenderer(gBuffer, renderbuffer)
    }

    @Subscribe
    fun onSizeChanged(event: ResizeEvent) {
        glViewport(0, 0, event.width, event.height)
    }

    fun renderFrame() {
        // Render scene to GBuffer
        renderGBuffer()

        // Transfer to render buffer using deferred shading
        renderbuffer.bind()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        bindGBuffer()
        gBuffer.fbo.blit(GL_DEPTH_BUFFER_BIT, renderbuffer.fbo)
        OpenGL.disable(GL_DEPTH_TEST)
        OpenGL.disable(GL_BLEND)
        OpenGL.depthMask(false)

        // Ambient lighting step
        ambientShader.bind()
        ambientShader["ambientStrength"] = scene.ambientStrength
        ambientShader["backgroundColor"] = scene.backgroundColor
        Primitives.fullscreenQuad.render()
        // ambientShader.unbind()

        // Blinn-Phong step (point lights)
        OpenGL.enable(GL_DEPTH_TEST)
        OpenGL.enable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)
        OpenGL.depthFunc(GL_GREATER)
        OpenGL.enable(GL_CULL_FACE)
        OpenGL.cullFace(GL_FRONT)

        blinnShader.bind()
        scene.rootNode.scanTree {
            if (it is PointLight && it.active) {
                blinnShader["modelMatrix"] = it.compositeTransform.matrix.scale(it.radius)
                blinnShader["light.position"] = it.compositeTransform.translation
                blinnShader["light.color"] = it.color
                blinnShader["light.constant"] = it.constant
                blinnShader["light.linear"] = it.linear
                blinnShader["light.quadratic"] = it.quadratic

                it.getPrimitive().render()
            }
        }
        blinnShader.unbind()
        OpenGL.depthMask(true)
        OpenGL.depthFunc(GL_LESS)

        // Prepare GL state for forward rendering
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        OpenGL.cullFace(GL_BACK)

        // Forward rendering
        scene.rootNode.scanTree { node ->
            if (node is RenderableNode && node.bucket == RenderBucket.Forward) {
                val shader = ShaderCache.get(node.material.shader)

                shader.bind()

                val modelMatrix = node.compositeTransform.matrix
                shader["modelMatrix"] = modelMatrix
                shader["normalMatrix"] = createNormalMatrix(modelMatrix)
                bindTexture(node.material[MatKey.TextureDiffuse])

                node.render()

                shader.unbind()
                OpenGL.resetState() // Clean up the crap that the shader may have left behind. Could probably be done more elegant.
            }
        }

        Events.post(RenderForwardEvent())
        renderbuffer.unbind()

        // Now, we can apply post processing and copy everything to the screen
        effectsRenderer.render()
    }

    private fun bindGBuffer() {
        gBuffer.fbo.getColorTexture(0).bind(0)
        gBuffer.fbo.getColorTexture(1).bind(1)
        gBuffer.fbo.getColorTexture(2).bind(2)
    }

    private fun renderGBuffer() {
        val deltaTime = NekoApp.the!!.timer.deltaTime

        gBuffer.bind()
        glClearColor(0f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        OpenGL.enable(GL_DEPTH_TEST)
        OpenGL.cullFace(GL_BACK)
        OpenGL.disable(GL_BLEND)

        scene.rootNode.scanTree { node ->
            if (node is RenderableNode && node.bucket == RenderBucket.Deferred) {
                val shader = ShaderCache.get(node.material.shader)

                OpenGL.setBoolean(GL_CULL_FACE, node.material[MatKey.TwoSided] == true)

                bindTexture(node.material[MatKey.TextureDiffuse])

                shader.bind()
                val modelMatrix = node.compositeTransform.matrix
                shader["modelMatrix"] = modelMatrix
                shader["normalMatrix"] = createNormalMatrix(modelMatrix)
                shader["specular"] = (node.material[MatKey.ColorSpecular] as? Color ?: Color.White).r
                shader["shininess"] = node.material[MatKey.Shininess] as? Float ?: 4.0f
                shader["diffuseColor"] = node.material[MatKey.ColorDiffuse] as? Color ?: Color.White

                node.getComponent<Animator>()?.let {
                    it.update(deltaTime)
                    it.loadMatrices(shader)
                }

                node.render()
            }
        }

        Events.post(RenderDeferredEvent())

        gBuffer.unbind()
    }

    private fun bindTexture(texture: Any?) {
        when (texture) {
            is Texture -> texture.bind()
            is String -> TextureCache.get(texture).bind()
            else -> StaticTextures.white.bind()
        }
    }

    private fun createNormalMatrix(modelMatrix: Matrix4f): Matrix3f {
        // //mat3 normalMatrix = mat3(transpose(inverse(modelMatrix)));
        return Matrix3f(modelMatrix).invert().transpose()
    }

}

