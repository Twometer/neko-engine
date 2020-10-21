package de.twometer.orion.render.shaders;

import de.twometer.orion.gl.Shader;
import de.twometer.orion.gl.Uniform;

public class VignetteShader extends Shader {

    public Uniform<Float> strength;

    public Uniform<Float> exponent;

    public VignetteShader() {
        super("PostVert.glsl", "PostVignette.glsl");
    }

}
