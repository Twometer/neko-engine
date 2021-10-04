package de.twometer.neko.render.pipeline

import de.twometer.neko.render.EffectsPipeline
import de.twometer.neko.render.FboManager
import de.twometer.neko.render.Primitives
import de.twometer.neko.render.StaticTextures
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.util.Profiler
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL30

class SSR : PipelineStep() {

    private val shader = ShaderCache.get("base/postproc.ssr.nks")

    private val buffer = FboManager.request({
        it.addColorTexture(0, GL30.GL_RGBA16F, GL30.GL_RGBA, GL30.GL_NEAREST, GL30.GL_FLOAT)
    }, 0.85f)

    override fun render(pipeline: EffectsPipeline) {
        Profiler.begin("Screen Space Reflections")
        pipeline.import("_Main").bind(4)
        pipeline.import("Bloom", StaticTextures.black).bind(5)
        shader.bind()
        buffer.bind()
        glClear(GL_COLOR_BUFFER_BIT)
        Primitives.unitQuad.render()
        pipeline.export("ScreenSpaceReflections", buffer.fbo.getColorTexture())
        Profiler.end()
    }


}