package de.twometer.neko.render

import de.twometer.neko.res.ShaderCache
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL30.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL30.GL_DEPTH_BUFFER_BIT

class EffectsRenderer(private val gBuffer: FramebufferRef, private val renderbuffer: FramebufferRef) {

    private val tonemapShader = ShaderCache.get("base/postproc.tonemap.nks")

    fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        renderbuffer.fbo.getColorTexture().bind()
        tonemapShader.bind()
        Primitives.fullscreenQuad.render()
        tonemapShader.unbind()
    }

}