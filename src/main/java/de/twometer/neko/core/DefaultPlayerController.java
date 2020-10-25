package de.twometer.neko.core;

import de.twometer.neko.gl.Window;
import de.twometer.neko.render.Camera;
import de.twometer.neko.util.MathF;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class DefaultPlayerController implements IPlayerController {

    private float speed;

    public DefaultPlayerController(float speed) {
        this.speed = speed;
    }

    @Override
    public void update(Window window, Camera camera) {
        if (window.isCursorVisible())
            return;

        float yaw = MathF.toRadians(camera.getAngle().x);
        Vector3f fwd = new Vector3f(MathF.sin(yaw), 0, MathF.cos(yaw)).normalize(speed);
        Vector3f left = new Vector3f(MathF.sin(yaw + MathF.PI / 2), 0, MathF.cos(yaw + MathF.PI / 2)).normalize(speed);

        if (window.isKeyPressed(GLFW_KEY_W))
            camera.getPosition().add(fwd);

        if (window.isKeyPressed(GLFW_KEY_A))
            camera.getPosition().add(left);

        if (window.isKeyPressed(GLFW_KEY_S))
            camera.getPosition().sub(fwd);

        if (window.isKeyPressed(GLFW_KEY_D))
            camera.getPosition().sub(left);

        if (window.isKeyPressed(GLFW_KEY_SPACE))
            camera.getPosition().add(new Vector3f(0, speed, 0));

        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
            camera.getPosition().add(new Vector3f(0, -speed, 0));

        Vector2f pos = window.getCursorPosition();
        Vector2f delta = pos.sub(new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f));
        camera.getAngle().add(new Vector2f(-delta.x * 0.04f, -delta.y * 0.04f));
        window.setCursorPosition(new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f));


        if (camera.getAngle().y > 90) camera.getAngle().y = 90f;
        if (camera.getAngle().y < -90) camera.getAngle().y = -90f;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

}
