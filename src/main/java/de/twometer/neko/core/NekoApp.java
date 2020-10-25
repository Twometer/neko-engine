package de.twometer.neko.core;

import de.twometer.neko.event.Events;
import de.twometer.neko.event.SizeChangedEvent;
import de.twometer.neko.gl.Framebuffer;
import de.twometer.neko.gl.Window;
import de.twometer.neko.gui.GuiManager;
import de.twometer.neko.gui.I18n;
import de.twometer.neko.render.Camera;
import de.twometer.neko.render.RenderManager;
import de.twometer.neko.render.Scene;
import de.twometer.neko.render.fx.FxManager;
import de.twometer.neko.render.overlay.OverlayManager;
import de.twometer.neko.render.pipeline.DeferredPipeline;
import de.twometer.neko.render.pipeline.PostRenderer;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;
import de.twometer.neko.sound.SoundFX;
import de.twometer.neko.util.*;
import org.greenrobot.eventbus.Subscribe;

import static org.lwjgl.opengl.GL11.*;

public abstract class NekoApp {

    private static NekoApp app;

    private Window window;
    private Timer timer;
    private Scene scene;
    private IPlayerController playerController = new DefaultPlayerController(0.125f);

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
    private final SoundFX soundFX = new SoundFX();

    /* Singleton */
    public NekoApp() {
        if (app == null)
            app = this;
        else
            throw new IllegalStateException("There may be only a single NekoApp instance");
    }

    public static NekoApp get() {
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
        soundFX.create();

        // Initial events
        Events.post(new SizeChangedEvent(window.getWidth(), window.getHeight()));

        onPreLoad();
        if (guiManager.getLoadingScreen() != null) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            guiManager.showPage(guiManager.getLoadingScreen());
            guiManager.render();
            guiManager.showPage(null);
            window.update();
        }

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
                playerController.update(window, camera);
                onTick();
            }

            camera.update();
            soundFX.update();
            renderManager.update();

            pipeline.render();
            getScene().getSkybox().render();
            onRender();
            getGuiManager().render();

            fpsCounter.count();
            window.update();
            fpsLimiter.sync();
        }

        destroy();
    }

    private void destroy() {
        Log.i("Shutting down...");

        window.destroy();
        soundFX.destroy();
        onDestroy();
        System.exit(0);
    }

    @Subscribe(priority = 1)
    public void onSizeChanged(SizeChangedEvent e) {
        Framebuffer.unbind();
        glViewport(0, 0, e.width, e.height);
    }

    /* Callbacks */
    protected void onPreLoad() {

    }

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

    public final void setScene(Scene scene) {
        this.scene = scene;
        scene.initialize();
    }

    public final Scene getScene() {
        return scene;
    }

    public final DeferredPipeline getPipeline() {
        return pipeline;
    }

    public final FxManager getFxManager() {
        return fxManager;
    }

    public final PostRenderer getPostRenderer() {
        return postRenderer;
    }

    public final RenderManager getRenderManager() {
        return renderManager;
    }

    public final OverlayManager getOverlayManager() {
        return overlayManager;
    }

    public final FpsLimiter getFpsLimiter() {
        return fpsLimiter;
    }

    public final FpsCounter getFpsCounter() {
        return fpsCounter;
    }

    public final GuiManager getGuiManager() {
        return guiManager;
    }

    public final SoundFX getSoundFX() {
        return soundFX;
    }

    public final I18n getI18n() {
        return guiManager.getI18n();
    }

    public final IPlayerController getPlayerController() {
        return playerController;
    }

    public final void setPlayerController(IPlayerController playerController) {
        this.playerController = playerController;
    }

}
