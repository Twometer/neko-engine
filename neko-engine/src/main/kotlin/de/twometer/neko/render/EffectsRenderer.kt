package de.twometer.neko.render

import de.twometer.neko.res.ShaderCache
import de.twometer.neko.util.MathF.lerp
import de.twometer.neko.util.MathF.rand
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import java.util.*

class EffectsRenderer(private val gBuffer: FramebufferRef, private val renderbuffer: FramebufferRef) {

    // TODO: Optimize SSAO to not draw 99% GPU power
    // TODO: Do not hardcode SSAO and use .nfx pipeline descriptions

    private val tonemapShader = ShaderCache.get("base/postproc.tonemap.nks")
    private val ssaoBaseShader = ShaderCache.get("base/postproc.ssao_base.nks")
    private val ssaoBlurShader = ShaderCache.get("base/postproc.ssao_blur.nks")

    private val ssaoBuffer = FboManager.request({
        it.addColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT)
    })
    private val ssaoBlurBuffer = FboManager.request({
        it.addColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT)
    })

    private val noiseTexture = generateNoiseTexture()
    private val sampleKernel = generateSampleKernel()

    init {
        ssaoBaseShader.bind()
        sampleKernel.forEachIndexed { i, v ->
            ssaoBaseShader["samples[$i]"] = v
        }
        ssaoBaseShader["kernelSize"] = 64
        ssaoBaseShader.unbind()
    }

    fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        OpenGL.disable(GL_DEPTH_TEST)
        OpenGL.disable(GL_CULL_FACE)

        //renderSSAO()
        renderNoSSAO()

        OpenGL.enable(GL_DEPTH_TEST)
    }

    fun renderNoSSAO() {
        renderbuffer.fbo.getColorTexture().bind()
        StaticTextures.white.bind(1)
        tonemapShader.bind()
        Primitives.fullscreenQuad.render()
    }

    fun renderSSAO() {
        gBuffer.fbo.getColorTexture(0).bind(0)
        gBuffer.fbo.getColorTexture(1).bind(1)
        gBuffer.fbo.getColorTexture(2).bind(2)
        noiseTexture.bind(4)

        ssaoBuffer.bind()
        ssaoBaseShader.bind()
        Primitives.fullscreenQuad.render()
        ssaoBaseShader.unbind()
        ssaoBuffer.unbind()

        ssaoBlurBuffer.bind()
        ssaoBlurShader.bind()
        ssaoBuffer.fbo.getColorTexture(0).bind(3)
        Primitives.fullscreenQuad.render()
        ssaoBuffer.unbind()
        ssaoBlurBuffer.unbind()

        renderbuffer.fbo.getColorTexture().bind()
        ssaoBlurBuffer.fbo.getColorTexture().bind(1)
        tonemapShader.bind()
        Primitives.fullscreenQuad.render()
        tonemapShader.unbind()
    }

    // Temporary SSAO util functions
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

    private fun generateSampleKernel(): List<Vector3f> {
        val ssaoKernel: MutableList<Vector3f> = ArrayList()
        for (i in 0..63) {
            var sample = Vector3f(
                rand() * 2.0f - 1.0f,
                rand() * 2.0f - 1.0f,
                rand()
            )
            var scale = i / 64.0f
            scale = lerp(0.1f, 1.0f, scale * scale)
            sample = sample.normalize(scale)
            ssaoKernel.add(sample)
        }
        return ssaoKernel
    }
}