package de.twometer.orion.render.shaders;

import de.twometer.orion.api.Dimensions;
import de.twometer.orion.api.Inject;
import de.twometer.orion.api.UniformInject;
import de.twometer.orion.gl.Shader;
import de.twometer.orion.gl.Uniform;
import de.twometer.orion.render.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

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
