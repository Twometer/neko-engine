package de.twometer.orion.core;

import de.twometer.orion.event.Events;
import de.twometer.orion.event.SizeChangedEvent;
import de.twometer.orion.gl.Framebuffer;
import de.twometer.orion.gl.Window;
import de.twometer.orion.gui.GuiManager;
import de.twometer.orion.render.Camera;
import de.twometer.orion.render.RenderManager;
import de.twometer.orion.render.Scene;
import de.twometer.orion.render.fx.FxManager;
import de.twometer.orion.render.overlay.OverlayManager;
import de.twometer.orion.render.pipeline.DeferredPipeline;
import de.twometer.orion.render.pipeline.PostRenderer;
import de.twometer.orion.res.cache.ShaderProvider;
import de.twometer.orion.res.cache.TextureProvider;
import de.twometer.orion.util.*;
import org.greenrobot.eventbus.Subscribe;

import static org.lwjgl.opengl.GL11.*;

public abstract class OrionApp {

    private static OrionApp app;

    private Window window;
    private Timer timer;
    private Scene scene;

    private final Camera camera = new Camera();
    private final FpsCounter fpsCounter = new FpsCounter();
    private final FpsLimiter fpsLimiter = new FpsLimiter();

    private final ShaderProvider shaderProvider = new ShaderProvider();
    private final TextureProvider textureProvider = new TextureProvider();

    private final GuiManager guiManager = new GuiManager();
    private final RenderManager renderManager = new RenderManager();
    private final DeferredPipeline pipeline = new DeferredPipeline();
    private final FxManager fxManager = new FxManager();
    private final OverlayManager overlayManager = new OverlayManager();
    private final PostRenderer postRenderer = new PostRenderer();

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
        launch(title, width, height, 45);
    }

    public final void launch(String title, int width, int height, int tps) {
        window = new Window(title, width, height);
        timer = new Timer(tps);
        runGameLoop();
    }

    /* Game loop internals */
    private void setup() {
        Log.i("Starting up...");
        CrashHandler.register();

        // Event system
        Events.init();
        Events.register(this);

        // GL Context
        window.create();

        // GL State
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0, 0, 0, 1);

        // Scene
        setScene(new Scene());

        // Services
        postRenderer.create();
        pipeline.create();
        fxManager.create();
        overlayManager.create();
        guiManager.create();

        // Initial events
        Events.post(new SizeChangedEvent(window.getWidth(), window.getHeight()));

        // User's init
        onInitialize();

        // Reset the timer
        timer.reset();
        Log.i("Initialization complete.");
    }

    private void runGameLoop() {
        setup();

        while (!window.shouldClose()) {
            if (timer.elapsed()) {
                timer.reset();
                camera.tick();
                onTick();
            }

            camera.update();
            renderManager.update();

            pipeline.render();
            getScene().getSkybox().render();
            onRender();

            fpsCounter.count();
            window.update();
            fpsLimiter.sync();
        }

        destroy();
    }

    private void destroy() {
        Log.i("Shutting down...");

        window.destroy();
        onDestroy();
        System.exit(0);
    }

    @Subscribe(priority = 1)
    public void onSizeChanged(SizeChangedEvent e) {
        Framebuffer.unbind();
        glViewport(0, 0, e.width, e.height);
    }

    /* Callbacks */
    protected void onInitialize() {

    }

    protected void onRender() {

    }

    protected void onTick() {

    }

    protected void onDestroy() {

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

    public void setScene(Scene scene) {
        this.scene = scene;
        scene.initialize();
    }

    public Scene getScene() {
        return scene;
    }

    public DeferredPipeline getPipeline() {
        return pipeline;
    }

    public FxManager getFxManager() {
        return fxManager;
    }

    public PostRenderer getPostRenderer() {
        return postRenderer;
    }

    public RenderManager getRenderManager() {
        return renderManager;
    }

    public OverlayManager getOverlayManager() {
        return overlayManager;
    }

    public FpsLimiter getFpsLimiter() {
        return fpsLimiter;
    }

    public FpsCounter getFpsCounter() {
        return fpsCounter;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

}
