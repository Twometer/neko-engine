package de.twometer.neko.render.shaders;

import de.twometer.neko.gl.Shader;

public class CopyShader extends Shader {

    public CopyShader() {
        super("PostVert.glsl", "PostCopy.glsl");
    }

}
