package de.twometer.orion.render.pipeline;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.gl.Framebuffer;
import de.twometer.orion.gl.Shader;
import de.twometer.orion.render.fx.SSAO;
import de.twometer.orion.render.fx.SSAOBlurShader;
import de.twometer.orion.render.fx.SSAOShader;
import de.twometer.orion.render.light.PointLight;
import de.twometer.orion.render.utils.PostProcessing;
import de.twometer.orion.util.Log;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class DeferredPipeline {

    private final PostProcessing postProcessing = new PostProcessing();

    private SSAOShader ssaoShader;
    private SSAOBlurShader ssaoBlurShader;
    private LightingShader lightingShader;

    private Framebuffer gBuffer;
    private Framebuffer ssaoBuffer;
    private Framebuffer ssaoBlurBuffer;

    private int ssaoNoise;

    public void create() {
        Log.d("Creating deferred pipeline");
        postProcessing.create();
        ssaoShader = OrionApp.get().getShaderProvider().getShader(SSAOShader.class);
        ssaoBlurShader = OrionApp.get().getShaderProvider().getShader(SSAOBlurShader.class);
        lightingShader = OrionApp.get().getShaderProvider().getShader(LightingShader.class);
        gBuffer = Framebuffer.create()
                .withDepthBuffer()
                .withColorTexture(0, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)   // Position
                .withColorTexture(1, GL_RGBA32F, GL_RGBA, GL_NEAREST, GL_FLOAT)   // Normals
                .withColorTexture(2)    // Albedo
                .finish();

        ssaoBuffer = Framebuffer.create()
                .withColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT) //, GL_RED, GL_RED, GL_NEAREST, GL_FLOAT
                .finish();
        ssaoBlurBuffer = Framebuffer.create()
                .withColorTexture(0, GL_RGBA16F, GL_RGBA, GL_NEAREST, GL_FLOAT)
                .finish();

        ssaoNoise = SSAO.generateNoiseTexture();

        List<Vector3f> ssaoKernel = SSAO.generateSampleKernel();
        ssaoShader.bind();
        for (int i = 0; i < ssaoKernel.size(); i++)
            ssaoShader.samples.set(i, ssaoKernel.get(i));
        Shader.unbind();
    }

    public void reloadLights() {
        List<PointLight> lights = OrionApp.get().getRenderManager().getLights();

        lightingShader.bind();
        lightingShader.numLights.set(lights.size());
        for (int i = 0; i < lights.size(); i++)
            lightingShader.lights.set(i, lights.get(i).getPosition());
    }

    public void begin() {
        gBuffer.bind();
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    }

    public void finish() {
        Framebuffer.unbind();

        postProcessing.begin();
        postProcessing.bindTexture(0, gBuffer.getColorTexture(0));
        postProcessing.bindTexture(1, gBuffer.getColorTexture(1));
        postProcessing.bindTexture(2, gBuffer.getColorTexture(2));
        postProcessing.bindTexture(4, ssaoNoise);

        ssaoShader.bind();
        postProcessing.copyTo(ssaoBuffer);
        postProcessing.bindTexture(3, ssaoBuffer.getColorTexture());

        ssaoBlurShader.bind();
        postProcessing.copyTo(ssaoBlurBuffer);
        postProcessing.bindTexture(3, ssaoBlurBuffer.getColorTexture());

        lightingShader.bind();
        lightingShader.viewPos.set(OrionApp.get().getCamera().getPosition());
        postProcessing.copyTo(null);

        postProcessing.end();
    }

}
