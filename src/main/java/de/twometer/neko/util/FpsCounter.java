package de.twometer.neko.util;

public class FpsCounter {

    private int fps;

    private int frames;

    private long lastReset = System.currentTimeMillis();

    public void count() {
        frames++;
        if (System.currentTimeMillis() - lastReset > 1000) {
            fps = frames;
            lastReset = System.currentTimeMillis();
            frames = 0;
            Log.d("FPS: " + fps);
        }
    }

    public int get() {
        return fps;
    }

}
