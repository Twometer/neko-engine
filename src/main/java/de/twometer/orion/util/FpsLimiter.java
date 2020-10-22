package de.twometer.orion.util;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class FpsLimiter {

    private int limit;

    private double frameTime;

    private boolean active;

    private double lastTime;

    public FpsLimiter() {
        setLimit(144);
        setActive(false);
    }

    public void sync() {
        if (active) {
            while (glfwGetTime() < lastTime + frameTime) {
                Thread.yield();
            }
            lastTime = glfwGetTime();
        }

    }

    public void setLimit(int limit) {
        this.limit = limit;
        this.frameTime = 1.0f / limit;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
