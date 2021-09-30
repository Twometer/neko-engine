package de.twometer.neko.render.pipeline

import de.twometer.neko.render.EffectsPipeline
import de.twometer.neko.render.FboManager
import de.twometer.neko.render.Primitives
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.util.Profiler

class Tonemap : PipelineStep() {

    private val shader = ShaderCache.get("base/postproc.tonemap.nks")
    private val buffer = FboManager.request({
        it.addColorTexture(0)
    })

    var gamma: Float = 0.98f
    var exposure: Float = 2f

    override fun render(pipeline: EffectsPipeline) {
        pipeline.import("_Main").bind(4)
        pipeline.import("AmbientOcclusion").bind(5)

        Profiler.begin("Tonemap")
        shader.bind()
        buffer.bind()
        shader["gamma"] = gamma
        shader["exposure"] = exposure
        Primitives.fullscreenQuad.render()
        Profiler.end()

        pipeline.export("_Main", buffer.fbo.getColorTexture())
    }

}