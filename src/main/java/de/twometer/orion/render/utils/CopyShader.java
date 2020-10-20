package de.twometer.orion.render.utils;

import de.twometer.orion.gl.Shader;

public class CopyShader extends Shader {

    public CopyShader() {
        super("PostVert.glsl", "PostCopy.glsl");
    }

}
