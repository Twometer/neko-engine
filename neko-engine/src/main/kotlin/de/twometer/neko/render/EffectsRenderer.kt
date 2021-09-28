package de.twometer.neko.render

import de.twometer.neko.core.NekoApp
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.util.MathF.rand
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*

class EffectsRenderer(private val gBuffer: FramebufferRef, private val renderbuffer: FramebufferRef) {

    private val tonemapShader = ShaderCache.get("base/postproc.tonemap.nks")
    private val aoBlurShader = ShaderCache.get("base/postproc.ao_blur.nks")
    private val aoBaseShader = ShaderCache.get("base/postproc.ao_base.nks")
    private val fxaaShader = ShaderCache.get("base/postproc.fxaa.nks")

    private val aoBaseBuffer = FboManager.request({
        it.addColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT)
    })
    private val aoBlurBuffer = FboManager.request({
        it.addColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT)
    })
    private val tonemapBuffer = FboManager.request({
        it.addColorTexture(0)
    })

    private val noiseTexture = generateNoiseTexture()

    fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        OpenGL.disable(GL_DEPTH_TEST)
        OpenGL.disable(GL_CULL_FACE)
        renderWithAO()
        OpenGL.enable(GL_DEPTH_TEST)
    }

    private val gamma = floatArrayOf(1.5f)
    private val exposure = floatArrayOf(1.5f)

    private fun renderWithAO() {
        gBuffer.fbo.getColorTexture(0).bind(0)
        gBuffer.fbo.getColorTexture(1).bind(1)
        gBuffer.fbo.getColorTexture(2).bind(2)
        noiseTexture.bind(4)

        // Build AO buffer
        aoBaseBuffer.bind()
        aoBaseShader.bind()
        aoBaseShader["uFOV"] = NekoApp.the!!.scene.camera.fov
        aoBaseShader["uIntensity"] = 0f
        aoBaseShader["uSampleRadiusWS"] = 0.46f
        aoBaseShader["uBias"] = 0.005f
        Primitives.fullscreenQuad.render()

        // Blur AO Buffer
        aoBlurBuffer.bind()
        aoBlurShader.bind()
        aoBaseBuffer.fbo.getColorTexture(0).bind(3)
        Primitives.fullscreenQuad.render()

        // Tonemap and multiply AO
        tonemapBuffer.bind()
        tonemapShader.bind()
        tonemapShader["gamma"] = 0.95f
        tonemapShader["exposure"] = 2f
        renderbuffer.fbo.getColorTexture().bind(0)
        aoBlurBuffer.fbo.getColorTexture().bind(1)
        Primitives.fullscreenQuad.render()
        tonemapBuffer.unbind()

        // Transfer to screen using FXAA
        fxaaShader.bind()
        tonemapBuffer.fbo.getColorTexture(0).bind(0)
        Primitives.fullscreenQuad.render()
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