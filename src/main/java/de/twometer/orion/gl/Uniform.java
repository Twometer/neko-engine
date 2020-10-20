package de.twometer.orion.gl;

import de.twometer.orion.api.UniformInject;
import de.twometer.orion.render.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class Uniform<T> {

    private FloatBuffer matrixBuffer;

    private final int location;

    private final int dimensionOverride;

    private final UniformInject uniformInject;

    Uniform(int location, int dimensionOverride, UniformInject uniformInject) {
        this.location = location;
        this.dimensionOverride = dimensionOverride;
        this.uniformInject = uniformInject;
    }

    public void set(T val) {
        if (val instanceof Color) {
            switch (dimensionOverride) {
                case 3 -> setColor3((Color) val);
                case 4 -> setColor4((Color) val);
                default -> throw new IllegalArgumentException("Color with " + dimensionOverride + " dimensions not allowed");
            }
        } else if (val instanceof Vector3f) {
            setVector3((Vector3f) val);
        } else if (val instanceof Vector4f) {
            setVector4((Vector4f) val);
        } else if (val instanceof Vector2f) {
            setVector2((Vector2f) val);
        } else if (val instanceof Float) {
            setFloat((Float) val);
        } else if (val instanceof Integer) {
            setInt((Integer) val);
        } else if (val instanceof Boolean) {
            setBool((Boolean) val);
        } else if (val instanceof Matrix4f) {
            setMatrix((Matrix4f) val);
        }
    }

    public UniformInject getInjectType() {
        return uniformInject;
    }

    private void setColor3(Color color) {
        setVector3(color.toVector3f());
    }

    private void setColor4(Color color) {
        setVector4(color.toVector4f());
    }

    private void setMatrix(Matrix4f mat) {
        if (mat == null)
            return;
        if (matrixBuffer == null)
            matrixBuffer = BufferUtils.createFloatBuffer(16);
        mat.get(matrixBuffer);
        glUniformMatrix4fv(location, false, matrixBuffer);
    }

    private void setVector4(Vector4f vec) {
        glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
    }

    private void setVector3(Vector3f vec) {
        glUniform3f(location, vec.x, vec.y, vec.z);
    }

    private void setVector2(Vector2f vec) {
        glUniform2f(location, vec.x, vec.y);
    }

    private void setFloat(float f) {
        glUniform1f(location, f);
    }

    private void setInt(int i) {
        glUniform1i(location, i);
    }

    private void setBool(boolean b) {
        glUniform1i(location, b ? 1 : 0);
    }
}
