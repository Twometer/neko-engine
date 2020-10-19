package de.twometer.orion.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {

    private Vector3f translation = new Vector3f(0, 0, 0);

    private Vector3f rotationOrigin = new Vector3f(0, 0, 0);

    private Vector3f rotation = new Vector3f(0, 0, 0);

    private Vector3f scale = new Vector3f(1, 1, 1);

    public Matrix4f getMatrix() {
        return new Matrix4f()
                .translate(-rotationOrigin.x, -rotationOrigin.y, -rotationOrigin.z)
                .rotateLocal(rotation.x, 1, 0, 0)
                .rotateLocal(rotation.y, 0, 1, 0)
                .rotateLocal(rotation.z, 0, 0, 1)
                .translateLocal(rotationOrigin)
                .translateLocal(translation)
                .scaleLocal(scale.x, scale.y, scale.z);
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public Vector3f getRotationOrigin() {
        return rotationOrigin;
    }

    public void setRotationOrigin(Vector3f rotationOrigin) {
        this.rotationOrigin = rotationOrigin;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public void set(Transform t) {
        translation.set(t.translation);
        rotationOrigin.set(t.rotationOrigin);
        rotation.set(t.rotation);
        scale.set(t.scale);
    }
}
