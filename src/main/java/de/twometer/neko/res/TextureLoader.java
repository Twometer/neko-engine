package de.twometer.neko.res;

import de.twometer.neko.gl.Cubemap;
import de.twometer.neko.gl.Texture;
import de.twometer.neko.util.CrashHandler;
import de.twometer.neko.util.Log;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader {

    /**
     * Load a cubemap comprised of 6 textures from disk
     *
     * @param textures Textures in order POSX (right), NEGX (left), POSY (top), NEGY (bottom), POSZ (back), NEGZ (front)
     * @return The cubemap texture object
     */
    public static Cubemap loadCubemap(String... textures) {
        Log.d("Loading cubemap " + Arrays.toString(textures));

        try {
            int textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);

            for (int i = 0; i < textures.length; i++) {
                var image = ResourceLoader.loadImage(AssetPaths.TEXTURE_PATH + textures[i]);
                var pixels = ResourceLoader.loadPixels(image);
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            }

            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
            glGenerateMipmap(GL_TEXTURE_CUBE_MAP);

            return new Cubemap(textureId);
        } catch (IOException e) {
            CrashHandler.fatal(e);
            return null;
        }
    }

    public static Texture loadTexture(String name) {
        Log.d("Loading texture " + name);
        var path = AssetPaths.TEXTURE_PATH + name;
        try {
            BufferedImage image = ResourceLoader.loadImage(path);
            ByteBuffer buffer = ResourceLoader.loadPixels(image);

            int textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glGenerateMipmap(GL_TEXTURE_2D);

            glBindTexture(GL_TEXTURE_2D, 0);

            return new Texture(textureId, image.getWidth(), image.getHeight());
        } catch (IOException e) {
            CrashHandler.fatal(e);
            return null;
        }
    }



}
