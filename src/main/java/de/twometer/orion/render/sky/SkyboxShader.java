package de.twometer.orion.render.sky;

import de.twometer.orion.api.Inject;
import de.twometer.orion.api.UniformInject;
import de.twometer.orion.gl.Shader;
import de.twometer.orion.gl.Uniform;
import org.joml.Matrix4f;

public class SkyboxShader extends Shader {

    public Uniform<Matrix4f> viewMatrix;

    @Inject(UniformInject.ProjMatrix)
    public Uniform<Matrix4f> projMatrix;

    public SkyboxShader() {
        super("Skybox");
    }

}
