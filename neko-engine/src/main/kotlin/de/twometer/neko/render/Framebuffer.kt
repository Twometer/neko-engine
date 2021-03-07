package de.twometer.neko.render

import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.NULL

class Framebuffer(val width: Int, val height: Int) {

    private val framebufferId: Int = glGenFramebuffers()

    private val colorAttachments = ArrayList<Int>()
    private val colorTextures = ArrayList<Int>()

    var depthTexture: Int = 0
        private set

    var depthBuffer: Int = 0
        private set

    fun bind() = glBindFramebuffer(GL_FRAMEBUFFER, framebufferId)

    fun unbind() = glBindFramebuffer(GL_FRAMEBUFFER, 0)

    fun addColorTexture(
        attachmentNum: Int = 0,
        internalFormat: Int = GL_RGBA8,
        format: Int = GL_RGBA,
        interpolation: Int = GL_LINE,
        dataType: Int = GL_UNSIGNED_BYTE
    ): Framebuffer {
        val attachment = GL_COLOR_ATTACHMENT0 + attachmentNum
        if (attachment in colorAttachments)
            error("Color attachment $attachmentNum defined twice on FBO #$framebufferId")

        val tex = glGenTextures()
        colorTextures.add(tex)
        colorAttachments.add(attachment)

        bind()
        glBindTexture(GL_TEXTURE_2D, tex)
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, dataType, NULL)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, interpolation)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, interpolation)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, tex, 0)
        glDrawBuffers(colorAttachments.toIntArray())
        unbind()
        return this
    }

    fun addDepthTexture(): Framebuffer {
        bind()
        depthTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, depthTexture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0)
        unbind()
        return this
    }

    fun addDepthBuffer(): Framebuffer {
        bind()
        depthBuffer = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer)
        unbind()
        return this
    }

    fun getColorTexture(num: Int = 0) = colorTextures[num]

}