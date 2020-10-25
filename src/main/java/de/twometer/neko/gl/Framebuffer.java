package de.twometer.neko.gl;

import de.twometer.neko.core.NekoApp;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Framebuffer {

    private final int width;
    private final int height;

    private final int framebuffer;
    private int depthBuffer;
    private int depthTexture;

    private final List<Integer> colorTextures = new ArrayList<>();
    private final List<Integer> colorAttachments = new ArrayList<>();

    Framebuffer(int width, int height, int framebuffer) {
        this.width = width;
        this.height = height;
        this.framebuffer = framebuffer;
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        glViewport(0, 0, width, height);
    }

    public static void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        var window = NekoApp.get().getWindow();
        glViewport(0, 0, window.getWidth(), window.getHeight());
    }

    public int getDepthBuffer() {
        return depthBuffer;
    }

    public int getDepthTexture() {
        return depthTexture;
    }

    public int getColorTexture() {
        return colorTextures.get(0);
    }

    public int getColorTexture(int i) {
        return colorTextures.get(i);
    }

    public static Framebuffer create() {
        var window = NekoApp.get().getWindow();
        return create(window.getWidth(), window.getHeight());
    }

    public Framebuffer finish() {
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new IllegalStateException("Framebuffer not complete");
        unbind();
        return this;
    }

    public static Framebuffer create(float width, float height) {
        return create((int) width, (int) height);
    }

    public static Framebuffer create(int width, int height) {
        int fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        return new Framebuffer(width, height, fbo);
    }

    public Framebuffer withColorTexture(int attachmentNum) {
        return withColorTexture(attachmentNum, GL_RGBA8, GL_RGBA, GL_LINEAR, GL_UNSIGNED_BYTE);
    }

    public Framebuffer withColorTexture(int attachmentNum, int internalFormat, int format, int interpolation, int dataType) {
        int attachment = GL_COLOR_ATTACHMENT0 + attachmentNum;
        if (colorTextures.contains(attachment)) {
            throw new IllegalArgumentException("Cannot create color attachment " + attachmentNum + "twice");
        }

        int tex = glGenTextures();
        colorAttachments.add(attachment);
        colorTextures.add(tex);

        glBindTexture(GL_TEXTURE_2D, tex);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, dataType, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, interpolation);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, interpolation);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, tex, 0);

        int[] colorAttachmentsArray = colorAttachments.stream().mapToInt(Integer::intValue).toArray();
        glDrawBuffers(colorAttachmentsArray);

        return this;
    }

    public Framebuffer withDepthTexture() {
        depthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
        return this;
    }

    public Framebuffer withDepthBuffer() {
        depthBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
        return this;
    }

    public void destroy() {
        glDeleteFramebuffers(framebuffer);
        for (int t : colorTextures)
            glDeleteTextures(t);
        glDeleteTextures(depthTexture);
        glDeleteRenderbuffers(depthBuffer);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void blitDepth(Framebuffer dst) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, dst == null ? 0 : dst.framebuffer);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
    }
}
