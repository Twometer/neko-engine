package de.twometer.neko.render

import de.twometer.neko.core.NekoApp
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.util.MathF.rand
import org.joml.Vector2f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*

class EffectsRenderer(private val gBuffer: FramebufferRef, private val renderbuffer: FramebufferRef) {

    // TODO: Do not hardcode SSAO and use .nfx pipeline descriptions

    private val tonemapShader = ShaderCache.get("base/postproc.tonemap.nks")
    private val aoBlurShader = ShaderCache.get("base/postproc.ao_blur.nks")
    private val aoBaseShader = ShaderCache.get("base/postproc.ao_base.nks")

    private val aoBuffer = FboManager.request({
        it.addColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT)
    })
    private val aoBlurBuffer = FboManager.request({
        it.addColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT)
    })

    private val noiseTexture = generateNoiseTexture()

    fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        OpenGL.disable(GL_DEPTH_TEST)
        OpenGL.disable(GL_CULL_FACE)

        //renderNoSSAO()
        renderSAO()

        OpenGL.enable(GL_DEPTH_TEST)
    }

    private fun renderNoSSAO() {
        renderbuffer.fbo.getColorTexture().bind()
        StaticTextures.white.bind(1)
        tonemapShader.bind()
        Primitives.fullscreenQuad.render()
    }

    private fun renderSAO() {
        gBuffer.fbo.getColorTexture(0).bind(0)
        gBuffer.fbo.getColorTexture(1).bind(1)
        gBuffer.fbo.getColorTexture(2).bind(2)
        noiseTexture.bind(4)

        aoBuffer.bind()
        aoBaseShader.bind()

        val (w, h) = NekoApp.the!!.window.getSize()
        aoBaseShader["uNoiseScale"] = Vector2f(w / 4f, h / 4f)
        aoBaseShader["uFOV"] = NekoApp.the!!.scene.camera.fov
        aoBaseShader["uIntensity"] = 0.5f
        aoBaseShader["uSampleRadiusWS"] = 0.45f
        aoBaseShader["uBias"] = 0.0f

        Primitives.fullscreenQuad.render()
        aoBaseShader.unbind()
        aoBuffer.unbind()

        aoBlurBuffer.bind()
        aoBlurShader.bind()
        aoBuffer.fbo.getColorTexture(0).bind(3)
        Primitives.fullscreenQuad.render()
        aoBuffer.unbind()
        aoBlurBuffer.unbind()

        renderbuffer.fbo.getColorTexture().bind()
        aoBlurBuffer.fbo.getColorTexture().bind(1)
        tonemapShader.bind()
        Primitives.fullscreenQuad.render()
        tonemapShader.unbind()
    }

    private fun generateNoiseTexture(): Texture2d {
        val buffer = BufferUtils.createFloatBuffer(16 * 3)
        for (i in 0..15) {
            buffer.put(rand() * 2.0f - 1.0f)
            buffer.put(rand() * 2.0f - 1.0f)
            buffer.put(0f)
        }
        buffer.flip()
        val texture = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 4, 4, 0, GL_RGB, GL_FLOAT, buffer)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        return Texture2d(texture, 4, 4)
    }
}