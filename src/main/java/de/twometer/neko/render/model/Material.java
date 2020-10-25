package de.twometer.neko.render.model;

import de.twometer.neko.gl.Texture;
import de.twometer.neko.render.Color;

public class Material {

    private final String name;

    private final Texture texture;

    private final Color diffuseColor;

    private final Color emissiveColor;

    public Material(String name, Texture texture, Color diffuseColor, Color emissiveColor) {
        this.name = name;
        this.texture = texture;
        this.diffuseColor = diffuseColor;
        this.emissiveColor = emissiveColor;
    }

    public String getName() {
        return name;
    }

    public Texture getTexture() {
        return texture;
    }

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    public Color getEmissiveColor() {
        return emissiveColor;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public boolean isEmissive() {
        return emissiveColor.getR() != 0;
    }

    public boolean isOpaque() {
        return diffuseColor.getA() == 1.0f;
    }
}
