package de.twometer.neko.render.light;

import org.joml.Vector3f;

public class LightSource {

    private final Vector3f position;

    private boolean on = true;

    public LightSource(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
