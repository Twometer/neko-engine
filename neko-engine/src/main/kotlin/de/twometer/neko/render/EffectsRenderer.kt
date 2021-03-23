package de.twometer.neko.render

import de.twometer.neko.res.ShaderCache
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL30.*

class EffectsRenderer {

    var gBuffer: Framebuffer? = null
    var renderbuffer: Framebuffer? = null

    private lateinit var tonemapShader: Shader

    fun setup() {
        tonemapShader = ShaderCache.get("base/postproc.tonemap.nks")
    }

    fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        renderbuffer!!.getColorTexture().bind()
        tonemapShader.bind()
        Primitives.fullscreenQuad.render()
        tonemapShader.unbind()
    }

}