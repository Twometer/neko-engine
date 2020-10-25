package de.twometer.neko.render.pipeline;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.event.Events;
import de.twometer.neko.event.SizeChangedEvent;
import de.twometer.neko.gl.GBuffer;
import de.twometer.neko.render.light.LightSource;
import de.twometer.neko.render.shading.DeferredShadingStrategy;
import de.twometer.neko.util.Log;
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

        lightingShader = NekoApp.get().getShaderProvider().getShader(LightingShader.class);
    }

    @Subscribe
    public void onSizeChanged(SizeChangedEvent e) {
        if (gBuffer != null) {
            gBuffer.destroy();
        }
        gBuffer = GBuffer.create();
    }

    public void reloadLights() {
        List<LightSource> lights = NekoApp.get().getScene().getLights();

        lightingShader.bind();
        lightingShader.numLights.set(lights.size());
        for (int i = 0; i < lights.size(); i++)
            lightingShader.lights.set(i, lights.get(i).getPosition());
    }

    public void render() {
        var app = NekoApp.get();

        // Setup
        gBuffer.bind();
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        // Geometry pass
        var scene = app.getScene();
        app.getRenderManager().setShadingStrategy(strategy);

        strategy.setPass(DeferredShadingStrategy.RenderPass.Opaque);
        scene.render();

        strategy.setPass(DeferredShadingStrategy.RenderPass.Translucent);
        scene.render();

        var bloom = app.getFxManager().getBloom();
        bloom.render();

        // Post processing
        var postRenderer = app.getPostRenderer();
        postRenderer.begin();
        gBuffer.bindTextures();

        var ssao = app.getFxManager().getSsao();
        ssao.render();

        lightingShader.bind();
        lightingShader.viewPos.set(app.getCamera().getPosition());
        postRenderer.bindTexture(3, ssao.getTexture());
        postRenderer.bindTexture(4, bloom.getTexture());

        var overlayManager = app.getOverlayManager();
        if (overlayManager.isEmtpy())
            postRenderer.copyTo(null);
        else {
            postRenderer.copyTo(overlayManager.getBuf0());
            overlayManager.render();
        }

        postRenderer.end();
        gBuffer.blitDepth(null);
    }

    public GBuffer getGBuffer() {
        return gBuffer;
    }

}
