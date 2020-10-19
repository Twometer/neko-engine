package de.twometer.orion.gl;

import de.twometer.orion.api.Uniform;
import de.twometer.orion.res.ShaderLoader;
import de.twometer.orion.util.Log;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1i;

public abstract class Shader {

    private final int id;

    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public Shader(String name) {
        this(name, name);
    }

    public Shader(String vertex, String fragment) {
        id = ShaderLoader.loadShader(vertex, fragment);
        bindUniforms();
    }

    private void bindUniforms() {
        bind();

        var fields = getClass().getDeclaredFields();
        for (var field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Uniform.class) && field.getType() == int.class) {
                Uniform uniform = field.getAnnotation(Uniform.class);
                int location = glGetUniformLocation(id, uniform.value());
                try {
                    field.set(this, location);
                } catch (IllegalAccessException e) {
                    Log.e("Failed to set uniform field", e);
                }
            }
        }

        unbind();
    }

    public final void bind() {
        glUseProgram(id);
    }

    public final void unbind() {
        glUseProgram(0);
    }

    protected final int getLocation(String name) {
        return glGetUniformLocation(id, name);
    }

    protected final void setMatrix(int location, Matrix4f mat) {
        mat.get(matrixBuffer);
        glUniformMatrix4fv(location, false, matrixBuffer);
    }

    protected final void setVector4(int location, Vector4f vec) {
        glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
    }

    protected final void setVector3(int location, Vector3f vec) {
        glUniform3f(location, vec.x, vec.y, vec.z);
    }

    protected final void setVector2(int location, Vector2f vec) {
        glUniform2f(location, vec.x, vec.y);
    }

    protected final void setFloat(int location, float f) {
        glUniform1f(location, f);
    }

    protected final void setInt(int location, int i) {
        glUniform1i(location, i);
    }
}
