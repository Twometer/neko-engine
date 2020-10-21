package de.twometer.orion.gl;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.pipeline.PostRenderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class GBuffer extends Framebuffer {

    GBuffer(int width, int height, int framebuffer) {
        super(width, height, framebuffer);
    }

    public static GBuffer create() {
        var window = OrionApp.get().getWindow();
        var fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        var buf = new GBuffer(window.getWidth(), window.getHeight(), fbo)
                .withDepthBuffer()
                .withColorTexture(0, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)   // Position
                .withColorTexture(1, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)   // Normals
                .withColorTexture(2)    // Albedo
                .finish();
        return (GBuffer) buf;
    }

    public int getPositionTexture() {
        return getColorTexture(0);
    }

    public int getNormalTexture() {
        return getColorTexture(1);
    }

    public int getAlbedoTexture() {
        return getColorTexture(2);
    }

    public void bindTextures() {
        var post = OrionApp.get().getPostRenderer();
        post.bindTexture(0, getPositionTexture());
        post.bindTexture(1, getNormalTexture());
        post.bindTexture(2, getAlbedoTexture());
    }

}
