package de.twometer.neko.render;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.gl.Window;
import de.twometer.neko.util.MathF;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f lastTickPosition = new Vector3f();
    private final Vector3f position = new Vector3f();

    private final Vector2f lastTickAngle = new Vector2f();
    private final Vector2f angle = new Vector2f();

    private Matrix4f viewMatrix;

    private Matrix4f projectionMatrix;

    private float fov = MathF.toRadians(70);

    private float near = 0.1f;

    private float far = 100.0f;

    public void update() {
        var partial = NekoApp.get().getTimer().getPartial();
        Vector3f posInterpolated = getInterpolatedPosition(partial);
        Vector2f angInterpolated = getInterpolatedAngle(partial);

        Window window = NekoApp.get().getWindow();
        float aspect = window.getWidth() / (float) window.getHeight();

        float yaw = MathF.toRadians(angInterpolated.x);
        float pitch = MathF.toRadians(angInterpolated.y);

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

        viewMatrix = new Matrix4f().lookAt(posInterpolated, direction.add(posInterpolated), up);
        projectionMatrix = new Matrix4f().perspective(fov, aspect, near, far);
    }

    public void tick() {
        lastTickPosition.set(position);
        lastTickAngle.set(angle);
    }

    public Vector3f getInterpolatedPosition(float partial) {
        return MathF.lerp(lastTickPosition, position, partial);
    }

    public Vector2f getInterpolatedAngle(float partial) {
        return MathF.lerp(lastTickAngle, angle, partial);
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

    public boolean ready() {
        return viewMatrix != null && projectionMatrix != null;
    }
}
