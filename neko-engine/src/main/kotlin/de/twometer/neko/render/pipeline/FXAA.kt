package de.twometer.neko.render.pipeline

import de.twometer.neko.render.FboManager
import de.twometer.neko.render.EffectsPipeline
import de.twometer.neko.render.Primitives
import de.twometer.neko.res.ShaderCache

class FXAA : PipelineStep() {

    private val shader = ShaderCache.get("base/postproc.fxaa.nks")
    private val buffer = FboManager.request({
        it.addColorTexture(0)
    })

    override fun render(pipeline: EffectsPipeline) {
        buffer.bind()
        shader.bind()
        pipeline.import("_Main").bind(4)
        Primitives.fullscreenQuad.render()
        pipeline.export("_Main", buffer.fbo.getColorTexture())
    }
}