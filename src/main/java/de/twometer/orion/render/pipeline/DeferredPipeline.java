package de.twometer.orion.render.pipeline;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.event.Events;
import de.twometer.orion.event.SizeChangedEvent;
import de.twometer.orion.gl.GBuffer;
import de.twometer.orion.render.light.LightSource;
import de.twometer.orion.render.shading.DeferredShadingStrategy;
import de.twometer.orion.util.Log;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class DeferredPipeline {

    private LightingShader lightingShader;

    private GBuffer gBuffer;

    private final DeferredShadingStrategy strategy = new DeferredShadingStrategy();

    public void create() {
        Log.d("Creating deferred rendering pipeline");
        Events.register(this);

        lightingShader = OrionApp.get().getShaderProvider().getShader(LightingShader.class);
    }

    @Subscribe
    public void onSizeChanged(SizeChangedEvent e) {
        if (gBuffer != null) {
            gBuffer.destroy();
        }
        gBuffer = GBuffer.create();
    }

    public void reloadLights() {
        List<LightSource> lights = OrionApp.get().getScene().getLights();

        lightingShader.bind();
        lightingShader.numLights.set(lights.size());
        for (int i = 0; i < lights.size(); i++)
            lightingShader.lights.set(i, lights.get(i).getPosition());
    }

    public void render() {
        var bloom = OrionApp.get().getFxManager().getBloom();
        bloom.render();

        // Setup
        gBuffer.bind();
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        // Geometry pass
        var scene = OrionApp.get().getScene();
        OrionApp.get().getRenderManager().setShadingStrategy(strategy);

        strategy.setPass(DeferredShadingStrategy.RenderPass.Opaque);
        scene.render();

        strategy.setPass(DeferredShadingStrategy.RenderPass.Translucent);
        scene.render();

        // Post processing
        var postRenderer = OrionApp.get().getPostRenderer();
        postRenderer.begin();
        gBuffer.bindTextures();

        var ssao = OrionApp.get().getFxManager().getSsao();
        ssao.render();

        lightingShader.bind();
        lightingShader.viewPos.set(OrionApp.get().getCamera().getPosition());
        postRenderer.bindTexture(3, ssao.getTexture());
        postRenderer.bindTexture(4, bloom.getTexture());
        postRenderer.copyTo(null);

        postRenderer.end();
        gBuffer.blitDepth(null);
    }

    public GBuffer getGBuffer() {
        return gBuffer;
    }
}
