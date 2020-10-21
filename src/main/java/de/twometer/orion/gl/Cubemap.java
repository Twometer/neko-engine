package de.twometer.orion.gl;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;

public class Cubemap {

    private final int textureId;

    public Cubemap(int textureId) {
        this.textureId = textureId;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
    }

    public static void unbind() {
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
    }

}
