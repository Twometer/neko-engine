package de.twometer.neko.render.pipeline

import de.twometer.neko.core.NekoApp
import de.twometer.neko.render.EffectsPipeline
import de.twometer.neko.render.FboManager
import de.twometer.neko.render.Primitives
import de.twometer.neko.render.StaticTextures
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.util.Profiler

class AmbientOcclusion : PipelineStep() {

    private val baseShader = ShaderCache.get("base/postproc.ao_base.nks")
    private val blurShader = ShaderCache.get("base/postproc.ao_blur.nks")

    private val baseBuffer = FboManager.request({
        it.addColorTexture(0)
    })
    private val blurBuffer = FboManager.request({
        it.addColorTexture(0)
    })

    var intensity: Float = 0f
    var radius: Float = 0.45f
    var bias: Float = 0.005f

    override fun render(pipeline: EffectsPipeline) {
        Profiler.begin("AO Base")
        baseBuffer.bind()
        baseShader.bind()
        baseShader["uFOV"] = NekoApp.the.scene.camera.fov
        baseShader["uIntensity"] = intensity
        baseShader["uSampleRadiusWS"] = radius
        baseShader["uBias"] = bias
        StaticTextures.noise5x5.bind(4)
        Primitives.unitQuad.render()
        Profiler.end()

        Profiler.begin("AO Blur")
        blurBuffer.bind()
        blurShader.bind()
        baseBuffer.fbo.getColorTexture(0).bind(4)
        Primitives.unitQuad.render()
        Profiler.end()

        pipeline.export("AmbientOcclusion", blurBuffer.fbo.getColorTexture())
    }

}