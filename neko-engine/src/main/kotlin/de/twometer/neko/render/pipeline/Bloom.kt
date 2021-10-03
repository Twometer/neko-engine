package de.twometer.neko.render.pipeline

import de.twometer.neko.render.EffectsPipeline
import de.twometer.neko.render.FboManager
import de.twometer.neko.render.Primitives
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.util.Profiler
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

class Bloom : PipelineStep() {

    private val baseShader = ShaderCache.get("base/postproc.bloom_base.nks")
    private val blurShader = ShaderCache.get("base/postproc.bloom_blur.nks")

    private val baseBuffer = FboManager.request({
        it.addColorTexture(0, GL30.GL_RGBA16F, GL30.GL_RGBA, GL30.GL_LINEAR, GL30.GL_FLOAT)
    }, 0.45f)
    private val blurBuffer = FboManager.request({
        it.addColorTexture(0, GL30.GL_RGBA16F, GL30.GL_RGBA, GL30.GL_LINEAR, GL30.GL_FLOAT)
    }, 1.0f)

    override fun render(pipeline: EffectsPipeline) {
        Profiler.begin("Bloom")
        baseShader.bind()
        baseBuffer.bind()
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        pipeline.import("_Main").bind(4)
        Primitives.fullscreenQuad.render()

        blurShader.bind()
        blurBuffer.bind()
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        baseBuffer.fbo.getColorTexture().bind(4)
        Primitives.fullscreenQuad.render()

        pipeline.export("Bloom", blurBuffer.fbo.getColorTexture())
        Profiler.end()
    }

}