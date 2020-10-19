package de.twometer.orion.render;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.gl.Window;
import de.twometer.orion.util.MathF;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position = new Vector3f(0, 0, 0);

    private final Vector2f angle = new Vector2f(0, 0);

    private Matrix4f viewMatrix;

    private Matrix4f projectionMatrix;

    private float fov = MathF.toRadians(70);

    private float near = 0.1f;

    private float far = 100.0f;

    public void update() {
        Window window = OrionApp.get().getWindow();
        float aspect = window.getWidth() / (float) window.getHeight();

        float yaw = MathF.toRadians(angle.x);
        float pitch = MathF.toRadians(angle.y);

        Vector3f direction = new Vector3f(
                MathF.cos(pitch) * MathF.sin(yaw),
                MathF.sin(pitch),
                MathF.cos(pitch) * MathF.cos(yaw)
        );
        Vector3f right = new Vector3f(
                MathF.sin(yaw - MathF.PI / 2.0f),
                0,
                MathF.cos(yaw - MathF.PI / 2.0f)
        );
        Vector3f up = new Vector3f(right).cross(direction);

        viewMatrix = new Matrix4f().lookAt(position, direction.add(position), up);
        projectionMatrix = new Matrix4f().perspective(fov, aspect, near, far);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getAngle() {
        return angle;
    }

    public void setFov(float fov) {
        this.fov = MathF.toRadians(fov);
    }

    public void setNear(float near) {
        this.near = near;
    }

    public void setFar(float far) {
        this.far = far;
    }
}
