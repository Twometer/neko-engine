package de.twometer.orion.core;

import de.twometer.orion.event.CharTypedEvent;
import de.twometer.orion.event.Events;
import de.twometer.orion.event.MouseClickedEvent;
import de.twometer.orion.event.SizeChangedEvent;
import de.twometer.orion.gl.Window;
import de.twometer.orion.render.Camera;
import de.twometer.orion.render.Scene;
import de.twometer.orion.render.model.ModelPart;
import de.twometer.orion.render.pipeline.DeferredPipeline;
import de.twometer.orion.render.shading.DeferredShadingStrategy;
import de.twometer.orion.res.cache.ShaderProvider;
import de.twometer.orion.res.cache.TextureProvider;
import de.twometer.orion.util.FpsCounter;
import de.twometer.orion.util.Log;
import de.twometer.orion.util.Timer;
import org.greenrobot.eventbus.EventBus;

import static org.lwjgl.opengl.GL11.*;

public abstract class OrionApp {

    private static OrionApp app;

    private Window window;

    private Timer timer;

    private final Scene scene = new Scene();

    private final Camera camera = new Camera();

    private final ShaderProvider shaderProvider = new ShaderProvider();

    private final TextureProvider textureProvider = new TextureProvider();

    private final FpsCounter fpsCounter = new FpsCounter();

    private final DeferredPipeline pipeline = new DeferredPipeline();

    /* Singleton */
    public OrionApp() {
        if (app == null)
            app = this;
        else
            throw new IllegalStateException("There may be only a single OrionApp instance");
    }

    public static OrionApp get() {
        return app;
    }

    /* Startup */

    public final void launch(String title, int width, int height) {
        launch(title, width, height, 90);
    }

    public final void launch(String title, int width, int height, int tps) {
        window = new Window(title, width, height);
        timer = new Timer(tps);
        runGameLoop();
    }

    /* Game loop internals */

    private void setup() {
        Log.i("Starting up...");

        Events.init();
        Events.register(this);

        window.create();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0, 0, 0, 1);
        pipeline.create();

        timer.reset();

        window.setCharTypedCallback(c -> Events.post(new CharTypedEvent(c)));
        window.setClickCallback(b -> Events.post(new MouseClickedEvent(b)));
        window.setSizeCallback(this::onResize);
        this.onResize(window.getWidth(), window.getHeight());

        onInitialize();
        Log.i("Initialization complete.");
    }

    private void runGameLoop() {
        setup();

        while (!window.shouldClose()) {
            camera.update();
            scene.update();

            if (timer.elapsed()) {
                timer.reset();
                onUpdate(timer.getPartial());
            }

            pipeline.render();

            onRenderForward();

            fpsCounter.count();
            window.update();
        }

        destroy();
    }

    private void destroy() {
        Log.i("Shutting down...");

        window.destroy();
        onDestroy();
        System.exit(0);
    }

    /* Callbacks */

    protected void onRenderForward() {

    }

    protected void onUpdate(float partial) {

    }

    protected void onInitialize() {

    }

    protected void onDestroy() {

    }

    protected void onResize(int w, int h) {
        glViewport(0, 0, w, h);
        Events.post(new SizeChangedEvent(w, h));
    }

    /* Accessors */

    public final Window getWindow() {
        return window;
    }

    public final Timer getTimer() {
        return timer;
    }

    public final Camera getCamera() {
        return camera;
    }

    public final ShaderProvider getShaderProvider() {
        return shaderProvider;
    }

    public final TextureProvider getTextureProvider() {
        return textureProvider;
    }

    public Scene getScene() {
        return scene;
    }

    public DeferredPipeline getPipeline() {
        return pipeline;
    }
}
