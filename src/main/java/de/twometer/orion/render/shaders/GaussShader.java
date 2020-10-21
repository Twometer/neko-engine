package de.twometer.orion.render.shaders;

import de.twometer.orion.gl.Shader;
import de.twometer.orion.gl.Uniform;

public class GaussShader extends Shader {

    public Uniform<Boolean> horizontal;

    public GaussShader() {
        super("PostVert.glsl", "PostGauss.glsl");
    }

}
