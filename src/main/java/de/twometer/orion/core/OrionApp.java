package de.twometer.orion.core;

import de.twometer.orion.gl.Window;
import de.twometer.orion.render.Camera;
import de.twometer.orion.util.Log;
import de.twometer.orion.util.Timer;

import static org.lwjgl.opengl.GL11.glViewport;

public abstract class OrionApp {

    private static OrionApp app;

    private Window window;

    private Timer timer;

    private Camera camera;

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
        launch(title, width, height, 30);
    }

    public final void launch(String title, int width, int height, int tps) {
        camera = new Camera();
        window = new Window(title, width, height);
        timer = new Timer(tps);
        runGameLoop();
    }

    /* Game loop internals */

    private void setup() {
        Log.i("Starting up...");

        window.create();
        window.setSizeCallback(this::onResize);

        timer.reset();

        onInitialize();
    }

    private void runGameLoop() {
        setup();

        while (!window.shouldClose()) {
            onRender();

            if (timer.elapsed()) {
                timer.reset();
                onUpdate(timer.getPartial());
            }

            camera.update();
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

    public void onRender() {

    }

    public void onUpdate(float partial) {

    }

    public void onInitialize() {

    }

    public void onDestroy() {

    }

    public void onResize(int w, int h) {
        glViewport(0, 0, w, h);
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
}
