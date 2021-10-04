package de.twometer.neko.render

import de.twometer.neko.core.NekoApp
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.NULL

class Framebuffer(val width: Int, val height: Int) {

    private val framebufferId: Int = glGenFramebuffers()

    private val colorAttachments = ArrayList<Int>()
    private val colorTextures = ArrayList<Texture2d>()

    var depthTexture: Texture2d? = null
        private set

    var depthBuffer: Int = 0
        private set

    fun bind(bufType: Int = GL_FRAMEBUFFER) {
        glBindFramebuffer(bufType, framebufferId)
        glViewport(0, 0, width, height)
    }

    fun unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        val (w, h) = NekoApp.the.window.getSize()
        glViewport(0, 0, w, h)
    }

    fun addColorTexture(
        attachmentNum: Int = 0,
        internalFormat: Int = GL_RGBA8,
        format: Int = GL_RGBA,
        interpolation: Int = GL_LINEAR,
        dataType: Int = GL_UNSIGNED_BYTE
    ): Framebuffer {
        val attachment = GL_COLOR_ATTACHMENT0 + attachmentNum
        if (attachment in colorAttachments)
            error("Color attachment $attachmentNum defined twice on FBO #$framebufferId")

        val tex = glGenTextures()
        colorTextures.add(Texture2d(tex, width, height))
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
        depthTexture = Texture2d(glGenTextures(), width, height)
        glBindTexture(GL_TEXTURE_2D, depthTexture!!.textureId)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture!!.textureId, 0)
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

    fun verify(): Framebuffer {
        bind()
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            error("Framebuffer not complete")
        unbind()
        return this
    }

    fun destroy() {
        glDeleteFramebuffers(framebufferId)
        for (t in colorTextures) glDeleteTextures(t.textureId)
        glDeleteTextures(depthTexture?.textureId ?: 0)
        glDeleteRenderbuffers(depthBuffer)
    }

    fun blit(mask: Int, target: Framebuffer? = null) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, framebufferId)
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target?.framebufferId ?: 0)
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, mask, GL_NEAREST)
    }

}