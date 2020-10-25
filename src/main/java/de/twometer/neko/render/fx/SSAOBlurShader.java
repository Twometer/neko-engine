package de.twometer.neko.render.fx;

import de.twometer.neko.gl.Shader;

public class SSAOBlurShader extends Shader {

    public SSAOBlurShader() {
        super("PostVert.glsl", "PostSSAOBlur.glsl");
    }

    @Override
    public void init() {
        bindSampler("ssaoInput", 3);
    }
}
