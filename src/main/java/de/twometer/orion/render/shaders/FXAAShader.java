package de.twometer.orion.render.shaders;

import de.twometer.orion.gl.Shader;

public class FXAAShader extends Shader {

    public FXAAShader() {
        super("PostVert.glsl", "PostFXAA.glsl");
    }

}
