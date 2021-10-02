package de.twometer.neko.render

import de.twometer.neko.render.pipeline.*
import de.twometer.neko.res.ShaderCache
import org.lwjgl.opengl.GL30.*

class EffectsPipeline(private val gBuffer: FramebufferRef, private val sceneBuffer: FramebufferRef) {

    private val copyShader = ShaderCache.get("base/postproc.copy.nks")
    private val texMap = HashMap<String, Texture2d>()
    val steps = ArrayList<PipelineStep>()

    init {
        steps.add(AmbientOcclusion())
        steps.add(SSR())
        steps.add(Tonemap())
        steps.add(FXAA())
    }

    fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        OpenGL.disable(GL_DEPTH_TEST)
        OpenGL.disable(GL_CULL_FACE)
        runPipeline()
        OpenGL.enable(GL_DEPTH_TEST)
    }

    fun import(texName: String, fallback: Texture2d = StaticTextures.white): Texture2d {
        return texMap[texName] ?: fallback
    }

    fun export(texName: String, texture: Texture2d) {
        texMap[texName] = texture
    }

    private fun runPipeline() {
        gBuffer.fbo.getColorTexture(0).bind(0)
        gBuffer.fbo.getColorTexture(1).bind(1)
        gBuffer.fbo.getColorTexture(2).bind(2)
        texMap.clear()
        export("_Main", sceneBuffer.fbo.getColorTexture())

        steps
            .filter { it.active }
            .forEach { it.render(this) }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        import("_Main").bind(4)
        copyShader.bind()
        Primitives.fullscreenQuad.render()
        copyShader.unbind()
    }

    inline fun <reified T> findStep(): T {
        for (step in steps)
            if (step is T)
                return step
        throw IllegalArgumentException("No pipeline step of type ${T::class.qualifiedName}")
    }

}