package example;

import de.twometer.orion.api.Uniform;
import de.twometer.orion.gl.Shader;
import org.joml.Matrix4f;

public class ExampleShader extends Shader {

    @Uniform("viewMatrix")
    private int loc_viewMatrix;

    @Uniform("projMatrix")
    private int loc_projMatrix;

    @Uniform("modelMatrix")
    private int loc_modelMatrix;

    public ExampleShader() {
        super("BaseVert.glsl", "BaseFrag.glsl");
    }

    public void setModelMatrix(Matrix4f mat) {
        setMatrix(loc_modelMatrix, mat);
    }

    public void setViewMatrix(Matrix4f mat) {
        setMatrix(loc_viewMatrix, mat);
    }

    public void setProjMatrix(Matrix4f mat) {
        setMatrix(loc_projMatrix, mat);
    }

}
