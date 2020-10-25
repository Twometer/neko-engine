package de.twometer.neko.render.fx;

import de.twometer.neko.api.Inject;
import de.twometer.neko.api.UniformInject;
import de.twometer.neko.gl.Shader;
import de.twometer.neko.gl.Uniform;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class SSAOShader extends Shader {

    @Inject(UniformInject.ProjMatrix)
    public Uniform<Matrix4f> projection;

    @Inject(UniformInject.ViewMatrix)
    public Uniform<Matrix4f> view;

    @Inject(UniformInject.ViewportSize)
    public Uniform<Vector2f> viewportSize;

    public Uniform<Integer> kernelSize;

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
