package de.twometer.orion.render.utils;

import de.twometer.orion.gl.Framebuffer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class PostProcessing {

    private final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};

    private int vao;

    public void create() {
        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, POSITIONS, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    public void begin() {
        glDisable(GL_DEPTH_TEST);
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
    }

    public void bindTexture(int unit, int tex) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, tex);
    }

    public void copyTo(Framebuffer target) {
        if (target != null) target.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        fullscreenQuad();
        if (target != null) target.unbind();
    }

    public void renderTo(Framebuffer target) {
        if (target != null) target.bind();
        fullscreenQuad();
        if (target != null) target.unbind();
    }

    public void fullscreenQuad() {
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    public void end() {
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        glActiveTexture(GL_TEXTURE0);
        glEnable(GL_DEPTH_TEST);
    }

}
