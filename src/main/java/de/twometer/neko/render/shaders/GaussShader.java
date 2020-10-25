package de.twometer.neko.render.shaders;

import de.twometer.neko.gl.Shader;
import de.twometer.neko.gl.Uniform;

public class GaussShader extends Shader {

    public Uniform<Boolean> horizontal;

    public GaussShader() {
        super("PostVert.glsl", "PostGauss.glsl");
    }

}
