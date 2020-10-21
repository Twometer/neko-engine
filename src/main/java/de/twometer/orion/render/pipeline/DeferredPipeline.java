package de.twometer.orion.render.pipeline;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.gl.GBuffer;
import de.twometer.orion.render.fx.SSAO;
import de.twometer.orion.render.light.PointLight;
import de.twometer.orion.util.Log;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class DeferredPipeline {

    private final PostRenderer postRenderer = new PostRenderer();

    private final SSAO ssao = new SSAO();

    private LightingShader lightingShader;

    private GBuffer gBuffer;


    public void create() {
        Log.d("Creating deferred pipeline");
        postRenderer.create();
        ssao.create();

        lightingShader = OrionApp.get().getShaderProvider().getShader(LightingShader.class);
        gBuffer = GBuffer.create();
    }

    public void reloadLights() {
        List<PointLight> lights = OrionApp.get().getScene().getLights();

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
        postRenderer.begin();
        gBuffer.bindTextures(postRenderer);

        ssao.render(postRenderer);

        lightingShader.bind();
        lightingShader.viewPos.set(OrionApp.get().getCamera().getPosition());
        postRenderer.bindTexture(3, ssao.getTexture());
        postRenderer.copyTo(null);

        postRenderer.end();
        gBuffer.blitDepth(null);
    }

    public SSAO getSsao() {
        return ssao;
    }
}
