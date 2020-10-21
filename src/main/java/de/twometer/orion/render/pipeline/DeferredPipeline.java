package de.twometer.orion.render.pipeline;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.event.Events;
import de.twometer.orion.event.SizeChangedEvent;
import de.twometer.orion.gl.GBuffer;
import de.twometer.orion.render.fx.SSAO;
import de.twometer.orion.render.light.PointLight;
import de.twometer.orion.render.shading.DeferredShadingStrategy;
import de.twometer.orion.util.Log;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class DeferredPipeline {

    private final PostRenderer postRenderer = new PostRenderer();

    private final SSAO ssao = new SSAO();

    private LightingShader lightingShader;

    private GBuffer gBuffer;

    private DeferredShadingStrategy strategy = new DeferredShadingStrategy();

    public void create() {
        Log.d("Creating deferred rendering pipeline");
        Events.register(this);

        postRenderer.create();
        ssao.create();

        lightingShader = OrionApp.get().getShaderProvider().getShader(LightingShader.class);
    }

    @Subscribe
    public void onSizeChanged(SizeChangedEvent e) {
        if (gBuffer != null) {
            gBuffer.destroy();
        }
        gBuffer = GBuffer.create();
        ssao.resize(e.width, e.height);
    }

    public void reloadLights() {
        List<PointLight> lights = OrionApp.get().getScene().getLights();

        lightingShader.bind();
        lightingShader.numLights.set(lights.size());
        for (int i = 0; i < lights.size(); i++)
            lightingShader.lights.set(i, lights.get(i).getPosition());
    }

    public void render() {
        // Setup
        gBuffer.bind();
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        // Geometry pass
        var scene = OrionApp.get().getScene();
        scene.setShadingStrategy(strategy);

        strategy.setPass(DeferredShadingStrategy.RenderPass.Solid);
        scene.renderFrame();

        strategy.setPass(DeferredShadingStrategy.RenderPass.Translucent);
        scene.renderFrame();

        // Post processing
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
