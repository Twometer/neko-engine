package de.twometer.orion.render.pipeline;

import de.twometer.orion.gl.Shader;
import de.twometer.orion.gl.Uniform;
import org.joml.Vector3f;

public class LightingShader extends Shader {

    public Uniform<Vector3f> lights;

    public Uniform<Integer> numLights;

    public Uniform<Vector3f> viewPos;

    public LightingShader() {
        super("PostVert.glsl", "PostLight.glsl");
    }

    @Override
    public void init() {
        bindSampler("gPosition", 0);
        bindSampler("gNormal", 1);
        bindSampler("gAlbedoSpec", 2);
        bindSampler("ssao", 3);
        bindSampler("bloom", 4);
    }
}
