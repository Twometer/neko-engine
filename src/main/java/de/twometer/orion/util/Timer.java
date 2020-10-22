package de.twometer.orion.util;

public class Timer {

    private final long delay;

    private long lastReset;

    public Timer(int tps) {
        this.delay = 1000000000 / tps;
        reset();
    }

    public void reset() {
        lastReset = System.nanoTime();
    }

    public boolean elapsed() {
        return System.nanoTime() - lastReset > delay;
    }

    public float getPartial() {
        float f = (lastReset + delay - System.nanoTime()) / (float) delay;
        return 1 - f;
    }

}
