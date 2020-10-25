package de.twometer.neko.render.shaders;

import de.twometer.neko.gl.Shader;
import de.twometer.neko.gl.Uniform;

public class VignetteShader extends Shader {

    public Uniform<Float> strength;

    public Uniform<Float> exponent;

    public VignetteShader() {
        super("PostVert.glsl", "PostVignette.glsl");
    }

}
