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
import org.joml.Vector3f
import org.lwjgl.opengl.GL30.*

class SceneRenderer(val scene: Scene) {

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
        glClearColor(0f, 0f, 0f, 1f)

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

        // Blinn-Phong step (point lights)
        blinnShader.bind()
        OpenGL.enable(GL_DEPTH_TEST)
        OpenGL.depthFunc(GL_GREATER)
        OpenGL.enable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)
        OpenGL.enable(GL_CULL_FACE)
        OpenGL.cullFace(GL_FRONT)

        val lights = ArrayList<PointLight>()

        scene.rootNode.scanTree {
            if (it is PointLight && it.active) {
                lights.add(it)
            }
        }

        for (j in 0 until lights.size step 32) {
            for (i in j until j + 32) {
                if (i >= lights.size)
                    break
                
                val it = lights[i]
                blinnShader["modelMatrices[$i]"] = it.compositeTransform.matrix.scale(it.radius)
                blinnShader["lightPositions[$i]"] = it.compositeTransform.translation
                blinnShader["lightColors[$i]"] = it.color
                blinnShader["lightValues[$i]"] = Vector3f(it.constant, it.linear, it.quadratic)
                blinnShader["test_id"] = i
            }
            Primitives.unitSphere.renderInstanced(32)
        }


        // Restore GL state
        OpenGL.depthMask(true)
        OpenGL.depthFunc(GL_LESS)
        OpenGL.cullFace(GL_BACK)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

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
                OpenGL.resetState() // Clean up the crap that the shader may have left behind. Could probably be done more elegant.
            }
        }

        OpenGL.useProgram(0)
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
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        OpenGL.enable(GL_DEPTH_TEST)
        OpenGL.cullFace(GL_BACK)
        OpenGL.disable(GL_BLEND)

        val nodes = ArrayList<RenderableNode>()

        // Collect nodes
        scene.rootNode.scanTree { node ->
            if (node is RenderableNode && node.bucket == RenderBucket.Deferred)
                nodes.add(node)
        }

        // Sort by material
        nodes.sortBy { it.material.shader }

        // Then render the node
        nodes.forEach { node ->
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

    private fun createNormalMatrix(modelMatrix: Matrix4f): Matrix3f = Matrix3f(modelMatrix).invert().transpose()

}

