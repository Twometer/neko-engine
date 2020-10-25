package de.twometer.neko.render.sky;

import de.twometer.neko.api.Inject;
import de.twometer.neko.api.UniformInject;
import de.twometer.neko.gl.Shader;
import de.twometer.neko.gl.Uniform;
import org.joml.Matrix4f;

public class SkyboxShader extends Shader {

    public Uniform<Matrix4f> viewMatrix;

    @Inject(UniformInject.ProjMatrix)
    public Uniform<Matrix4f> projMatrix;

    public SkyboxShader() {
        super("Skybox");
    }

}
