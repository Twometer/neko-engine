package de.twometer.orion.render.light;

import org.joml.Vector3f;

public class LightSource {

    private final Vector3f position;

    public LightSource(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

}
