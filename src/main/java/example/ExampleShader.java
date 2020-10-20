package example;

import de.twometer.orion.api.Dimensions;
import de.twometer.orion.api.Inject;
import de.twometer.orion.api.UniformInject;
import de.twometer.orion.gl.Shader;
import de.twometer.orion.gl.Uniform;
import de.twometer.orion.render.Color;
import org.joml.Matrix4f;

public class ExampleShader extends Shader {

    @Inject(UniformInject.ViewMatrix)
    public Uniform<Matrix4f> viewMatrix;

    @Inject(UniformInject.ProjMatrix)
    public Uniform<Matrix4f> projMatrix;

    public Uniform<Matrix4f> modelMatrix;

    @Dimensions(4)
    public Uniform<Color> modelColor;

    public Uniform<Boolean> hasTexture;

    public ExampleShader() {
        super("ExampleVert.glsl", "ExampleFrag.glsl");
    }

}
