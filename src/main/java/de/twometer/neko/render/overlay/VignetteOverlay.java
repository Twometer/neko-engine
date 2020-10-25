package de.twometer.neko.render.overlay;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.shaders.VignetteShader;

public class VignetteOverlay implements IOverlay {

    private final VignetteShader shader;

    private float strength;

    private float exponent;

    public VignetteOverlay(float strength, float exponent) {
        this.strength = strength;
        this.exponent = exponent;
        this.shader = NekoApp.get().getShaderProvider().getShader(VignetteShader.class);
    }

    @Override
    public void setupShader() {
        shader.bind();
        shader.strength.set(strength);
        shader.exponent.set(exponent);
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

}
