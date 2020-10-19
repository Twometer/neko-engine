package de.twometer.orion.render.model;

import de.twometer.orion.render.Color;
import org.joml.Vector3f;

public class Material {

    private final String name;

    private final String texture;

    private final Color diffuseColor;

    private final Color emissiveColor;

    public Material(String name, String texture, Color diffuseColor, Color emissiveColor) {
        this.name = name;
        this.texture = texture;
        this.diffuseColor = diffuseColor;
        this.emissiveColor = emissiveColor;
    }

    public String getName() {
        return name;
    }

    public String getTexture() {
        return texture;
    }

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    public Color getEmissiveColor() {
        return emissiveColor;
    }
}
