package de.twometer.neko.render

import de.twometer.neko.events.Events
import de.twometer.neko.events.RenderDeferredEvent
import de.twometer.neko.events.RenderForwardEvent
import de.twometer.neko.events.ResizeEvent
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.res.TextureCache
import de.twometer.neko.scene.*
import org.greenrobot.eventbus.Subscribe
import org.lwjgl.opengl.GL30.*

class SceneRenderer(val scene: Scene) {

    private var gBuffer: Framebuffer? = null
    private var renderbuffer: Framebuffer? = null
    lateinit var blinnShader: Shader
    lateinit var ambientShader: Shader
    lateinit var gammaCorrectShader: Shader

    fun setup() {
        Events.register(this)

        // Shaders
        blinnShader = ShaderCache.get("base/lighting.blinn.nks")
        ambientShader = ShaderCache.get("base/lighting.ambient.nks")
        gammaCorrectShader = ShaderCache.get("base/postproc.gamma.nks")
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

        renderbuffer?.destroy()
        renderbuffer = Framebuffer(event.width, event.height)
            .addDepthBuffer()
            .addColorTexture(0, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)
            .verify()
    }

    fun renderFrame() {
        // Render scene to GBuffer
        renderGBuffer()

        // Transfer to render buffer using deferred shading
        renderbuffer!!.bind()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        OpenGL.disable(GL_DEPTH_TEST)
        OpenGL.depthMask(false)

        bindGBuffer()

        // Ambient lighting step
        ambientShader.bind()
        ambientShader["ambientStrength"] = scene.ambientStrength
        ambientShader["backgroundColor"] = scene.backgroundColor
        Primitives.fullscreenQuad.render()
        ambientShader.unbind()

        // Blinn-Phong step (point lights)
        OpenGL.enable(GL_BLEND)
        OpenGL.enable(GL_CULL_FACE)
        OpenGL.cullFace(GL_FRONT)
        glBlendFunc(GL_ONE, GL_ONE)

        blinnShader.bind()
        scene.rootNode.scanTree {
            if (it is PointLight && it.active) {
                blinnShader["modelMatrix"] = it.compositeTransform.matrix.scale(it.radius)
                blinnShader["light.position"] = it.compositeTransform.translation
                blinnShader["light.color"] = it.color
                blinnShader["light.constant"] = it.constant
                blinnShader["light.linear"] = it.linear
                blinnShader["light.quadratic"] = it.quadratic

                Primitives.unitSphere.render()
            }
        }
        blinnShader.unbind()

        // Copy depth buffer from GBuffer to main FBO
        OpenGL.depthMask(true)
        gBuffer!!.blit(GL_DEPTH_BUFFER_BIT, renderbuffer)

        // Prepare GL state for forward rendering
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        OpenGL.cullFace(GL_BACK)
        OpenGL.enable(GL_DEPTH_TEST)

        // Forward rendering
        scene.rootNode.scanTree { node ->
            if (node is Renderable && node.bucket == RenderBucket.Forward) {
                val shader = ShaderCache.get(node.material.shader)

                shader.bind()
                shader["modelMatrix"] = node.compositeTransform.matrix
                bindTexture(node.material[MatKey.TextureDiffuse])

                node.render()

                shader.unbind()
                OpenGL.resetState() // Clean up the crap that the shader may have left behind. Could probably be done more elegant.
            }
        }

        Events.post(RenderForwardEvent())
        renderbuffer!!.unbind()

        // Now, we can apply postproc to our nice renderbuffer
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        renderbuffer!!.getColorTexture().bind()

        gammaCorrectShader.bind()
        Primitives.fullscreenQuad.render()
        gammaCorrectShader.unbind()
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

        OpenGL.enable(GL_DEPTH_TEST)
        OpenGL.cullFace(GL_BACK)
        OpenGL.disable(GL_BLEND)

        scene.rootNode.scanTree { node ->
            if (node is Renderable && node.bucket == RenderBucket.Deferred) {
                val shader = ShaderCache.get(node.material.shader)

                OpenGL.setBoolean(GL_CULL_FACE, node.material[MatKey.TwoSided] == true)

                bindTexture(node.material[MatKey.TextureDiffuse])

                shader.bind()
                shader["modelMatrix"] = node.compositeTransform.matrix
                shader["specular"] = (node.material[MatKey.ColorSpecular] as? Color ?: Color.White).r
                shader["shininess"] = node.material[MatKey.Shininess] as? Float ?: 4.0f

                node.render()
            }
        }

        Events.post(RenderDeferredEvent())

        gBuffer!!.unbind()
    }

    private fun bindTexture(texture: Any?) {
        when (texture) {
            is Cubemap -> texture.bind()
            is Texture -> texture.bind()
            is String -> TextureCache.get(texture).bind()
        }
    }

}
