package de.twometer.neko.render.fx;

import de.twometer.neko.util.MathF;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

public class SSAOUtil {

    public static int generateNoiseTexture() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16 * 3);
        for (int i = 0; i < 16; i++) {
            buffer.put(MathF.rand() * 2.0f - 1.0f);
            buffer.put(MathF.rand() * 2.0f - 1.0f);
            buffer.put(0);
        }

        buffer.flip();
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 4, 4, 0, GL_RGB, GL_FLOAT, buffer);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        return texture;
    }

    public static List<Vector3f> generateSampleKernel() {
        List<Vector3f> ssaoKernel = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            Vector3f sample = new Vector3f(
                    MathF.rand() * 2.0f - 1.0f,
                    MathF.rand() * 2.0f - 1.0f,
                    MathF.rand()
            );
            float scale = i / 64.0f;
            scale = MathF.lerp(0.1f, 1.0f, scale * scale);
            sample = sample.normalize(scale);
            ssaoKernel.add(sample);
        }
        return ssaoKernel;
    }

}
