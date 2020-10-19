package de.twometer.orion.util;

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
        }
    }

    public int get() {
        return fps;
    }

}
