package de.twometer.neko.render

import org.lwjgl.opengl.GL11.*

enum class ShaderProperty(val apply: (String) -> Unit) {

    DepthTest({
        OpenGL.setBoolean(GL_DEPTH_TEST, it == on)
    }),

    DepthMask({
        OpenGL.depthMask(it == on)
    }),

    DepthFunc({
        OpenGL.depthFunc(openGlMapping[it]!!)
    }),

    CullFace({
        if (it == off) {
            OpenGL.disable(GL_CULL_FACE)
        } else {
            OpenGL.enable(GL_CULL_FACE)
            OpenGL.cullFace(openGlMapping[it]!!)
        }
    })

    ;

    companion object {

        private val openGlMapping = hashMapOf(
            Pair("Back", GL_BACK),
            Pair("Front", GL_FRONT),
            Pair("LEqual", GL_LEQUAL),
            Pair("Less", GL_LESS),
            Pair("Equal", GL_EQUAL),
            Pair("GEqual", GL_GEQUAL),
            Pair("Greater", GL_GREATER)
        )

        private const val on = "On"
        private const val off = "Off"
    }

}