package de.twometer.orion.render.light;

import org.joml.Vector3f;

public class PointLight {

    private final Vector3f position;

    public PointLight(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

}
