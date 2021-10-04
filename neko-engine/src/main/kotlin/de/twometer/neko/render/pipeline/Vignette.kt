package de.twometer.neko.render.pipeline

import de.twometer.neko.render.EffectsPipeline
import de.twometer.neko.render.FboManager
import de.twometer.neko.render.Primitives
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.util.Profiler

class Vignette : PipelineStep() {

    private val shader = ShaderCache.get("base/postproc.vignette.nks")
    private val buffer = FboManager.request({
        it.addColorTexture()
    })

    var strength = 20.0f
    var exponent = 0.15f

    override fun render(pipeline: EffectsPipeline) {
        Profiler.begin("Vignette")
        buffer.bind()
        shader.bind()
        shader["strength"] = strength
        shader["exponent"] = exponent
        pipeline.import("_Main").bind(4)
        Primitives.unitQuad.render()
        pipeline.export("_Main", buffer.fbo.getColorTexture())
        Profiler.end()
    }

}