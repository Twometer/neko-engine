package de.twometer.neko.render.shaders;

import de.twometer.neko.api.Dimensions;
import de.twometer.neko.api.Inject;
import de.twometer.neko.api.UniformInject;
import de.twometer.neko.gl.Shader;
import de.twometer.neko.gl.Uniform;
import de.twometer.neko.render.Color;
import org.joml.Matrix4f;

public class FlatShader extends Shader {

    @Inject(UniformInject.ProjMatrix)
    public Uniform<Matrix4f> projMatrix;

    @Inject(UniformInject.ViewMatrix)
    public Uniform<Matrix4f> viewMatrix;

    public Uniform<Matrix4f> modelMatrix;

    @Dimensions(4)
    public Uniform<Color> modelColor;

    public FlatShader() {
        super("Flat");
    }

}
