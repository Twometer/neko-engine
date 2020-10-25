package de.twometer.neko.render.shaders;

import de.twometer.neko.gl.Shader;

public class FXAAShader extends Shader {

    public FXAAShader() {
        super("PostVert.glsl", "PostFXAA.glsl");
    }

}
