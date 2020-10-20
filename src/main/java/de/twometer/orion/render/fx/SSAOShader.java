package de.twometer.orion.render.fx;

import de.twometer.orion.api.Inject;
import de.twometer.orion.api.UniformInject;
import de.twometer.orion.gl.Shader;
import de.twometer.orion.gl.Uniform;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SSAOShader extends Shader {

    @Inject(UniformInject.ProjMatrix)
    public Uniform<Matrix4f> projection;

    @Inject(UniformInject.ViewMatrix)
    public Uniform<Matrix4f> view;

    public Uniform<Vector3f> samples;

    public SSAOShader() {
        super("PostVert.glsl", "PostSSAO.glsl");
    }

    @Override
    public void init() {
        bindSampler("gPosition", 0);
        bindSampler("gNormal", 1);
        bindSampler("noise", 4);
    }
}
