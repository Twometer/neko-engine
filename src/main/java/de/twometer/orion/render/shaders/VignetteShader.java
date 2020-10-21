package de.twometer.orion.render.shaders;

import de.twometer.orion.gl.Shader;

public class VignetteShader extends Shader {

    public VignetteShader() {
        super("PostVert.glsl", "PostVignette.glsl");
    }

}
