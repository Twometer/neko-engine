package de.twometer.orion.render.fx;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.gl.Framebuffer;
import de.twometer.orion.gl.Shader;
import de.twometer.orion.render.pipeline.PostRenderer;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

public class SSAO extends FXBase {

    private SSAOShader ssaoShader;
    private SSAOBlurShader ssaoBlurShader;

    private Framebuffer ssaoBuffer;
    private Framebuffer ssaoBlurBuffer;

    private int ssaoNoise;

    private int samples;

    private float scale = 1.0f;

    public void create() {
        ssaoShader = OrionApp.get().getShaderProvider().getShader(SSAOShader.class);
        ssaoBlurShader = OrionApp.get().getShaderProvider().getShader(SSAOBlurShader.class);

        ssaoNoise = SSAOUtil.generateNoiseTexture();

        List<Vector3f> ssaoKernel = SSAOUtil.generateSampleKernel();
        ssaoShader.bind();
        for (int i = 0; i < ssaoKernel.size(); i++)
            ssaoShader.samples.set(i, ssaoKernel.get(i));
        Shader.unbind();
    }

    void renderImpl(PostRenderer post) {
        if (!isActive())
            return;

        post.bindTexture(4, ssaoNoise);

        ssaoShader.bind();
        ssaoShader.kernelSize.set(samples);
        post.copyTo(ssaoBuffer);
        post.bindTexture(3, ssaoBuffer.getColorTexture());

        ssaoBlurShader.bind();
        post.copyTo(ssaoBlurBuffer);
    }

    @Override
    public void resize(int w, int h) {
        ssaoBuffer = Framebuffer.create(w * scale, h * scale)
                .withColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT)
                .finish();
        ssaoBlurBuffer = Framebuffer.create(w * scale, h * scale)
                .withColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT)
                .finish();

        ssaoBlurBuffer.bind();
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(0, 0, 0, 1);
        Framebuffer.unbind();
    }

    public int getTexture() {
        return ssaoBlurBuffer.getColorTexture();
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
