package de.twometer.neko.render.pipeline;

import de.twometer.neko.api.Dimensions;
import de.twometer.neko.api.Inject;
import de.twometer.neko.api.UniformInject;
import de.twometer.neko.gl.Shader;
import de.twometer.neko.gl.Uniform;
import de.twometer.neko.render.Color;
import org.joml.Matrix4f;

public class DeferredShader extends Shader {

    @Inject(UniformInject.ViewMatrix)
    public Uniform<Matrix4f> viewMatrix;

    @Inject(UniformInject.ProjMatrix)
    public Uniform<Matrix4f> projMatrix;

    public Uniform<Matrix4f> modelMatrix;

    @Dimensions(4)
    public Uniform<Color> modelColor;

    public Uniform<Boolean> hasTexture;

    public DeferredShader() {
        super("Deferred");
    }

}
