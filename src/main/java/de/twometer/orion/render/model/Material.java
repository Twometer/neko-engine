package de.twometer.orion.render.model;

import org.joml.Vector3f;

public class Material {

    private final String name;

    private final String texture;

    private final Vector3f diffuseColor;

    public Material(String name, String texture, Vector3f diffuseColor) {
        this.name = name;
        this.texture = texture;
        this.diffuseColor = diffuseColor;
    }

    public String getTexture() {
        return texture;
    }

    public Vector3f getDiffuseColor() {
        return diffuseColor;
    }

    public String getName() {
        return name;
    }
}
