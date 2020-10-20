package de.twometer.orion.render.pipeline;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.gl.Framebuffer;
import de.twometer.orion.render.utils.CopyShader;
import de.twometer.orion.render.utils.PostProcessing;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

public class DeferredPipeline {

    private final PostProcessing postProcessing = new PostProcessing();

    private LightingShader lightingShader;

    private Framebuffer gBuffer;

    public void reloadLights() {
        var lights = OrionApp.get().getRenderManager().getLights();

        lightingShader.bind();
        lightingShader.numLights.set(lights.size());
        for (int i = 0; i < lights.size(); i++)
            lightingShader.lights.set(i, lights.get(i).getPosition());
    }

    public void create() {
        postProcessing.create();
        lightingShader = OrionApp.get().getShaderProvider().getShader(LightingShader.class);
        gBuffer = Framebuffer.create()
                .withDepthBuffer()
                .withColorTexture(0, GL_RGBA16F, GL_NEAREST, GL_FLOAT)   // Position
                .withColorTexture(1, GL_RGBA16F, GL_NEAREST, GL_FLOAT)   // Normals
                .withColorTexture(2)    // Albedo
                .finish();
    }

    public void begin() {
        gBuffer.bind();
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    }

    public void finish() {
        Framebuffer.unbind();

        postProcessing.begin();

        lightingShader.bind();
        lightingShader.viewPos.set(OrionApp.get().getCamera().getPosition());
        postProcessing.bindTexture(0, gBuffer.getColorTexture(0));
        postProcessing.bindTexture(1, gBuffer.getColorTexture(1));
        postProcessing.bindTexture(2, gBuffer.getColorTexture(2));
        postProcessing.copyTo(null);
        postProcessing.end();
    }

}
