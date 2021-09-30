package de.twometer.neko.render.pipeline

import de.twometer.neko.core.NekoApp
import de.twometer.neko.render.FboManager
import de.twometer.neko.render.EffectsPipeline
import de.twometer.neko.render.Primitives
import de.twometer.neko.render.StaticTextures
import de.twometer.neko.res.ShaderCache
import org.lwjgl.opengl.GL30

class AmbientOcclusion : PipelineStep() {

    private val baseShader = ShaderCache.get("base/postproc.ao_base.nks")
    private val blurShader = ShaderCache.get("base/postproc.ao_blur.nks")

    private val baseBuffer = FboManager.request({
        it.addColorTexture(0, GL30.GL_RGBA16F, GL30.GL_RGBA, GL30.GL_NEAREST, GL30.GL_FLOAT)
    })
    private val blurBuffer = FboManager.request({
        it.addColorTexture(0, GL30.GL_RGBA16F, GL30.GL_RGBA, GL30.GL_NEAREST, GL30.GL_FLOAT)
    })

    var intensity: Float = 0f
    var radius: Float = 0.45f
    var bias: Float = 0.005f

    override fun render(pipeline: EffectsPipeline) {
        baseBuffer.bind()
        baseShader.bind()
        baseShader["uFOV"] = NekoApp.the!!.scene.camera.fov
        baseShader["uIntensity"] = intensity
        baseShader["uSampleRadiusWS"] = radius
        baseShader["uBias"] = bias
        StaticTextures.noise4x4.bind(4)
        Primitives.fullscreenQuad.render()

        blurBuffer.bind()
        blurShader.bind()
        baseBuffer.fbo.getColorTexture(0).bind(4)
        Primitives.fullscreenQuad.render()

        pipeline.export("AmbientOcclusion", blurBuffer.fbo.getColorTexture())
    }

}