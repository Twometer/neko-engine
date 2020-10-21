package de.twometer.orion.render.post;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.event.Events;
import de.twometer.orion.event.SizeChangedEvent;
import de.twometer.orion.gl.Framebuffer;
import de.twometer.orion.render.shaders.VignetteShader;
import org.greenrobot.eventbus.Subscribe;

import java.awt.image.VolatileImage;

public class VignettePostFx implements IPostFx {

    private final VignetteShader shader;

    private float strength;

    private float exponent;

    public VignettePostFx(float strength, float exponent) {
        this.strength = strength;
        this.exponent = exponent;
        this.shader = OrionApp.get().getShaderProvider().getShader(VignetteShader.class);
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
