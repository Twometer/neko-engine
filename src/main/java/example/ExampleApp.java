package example;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.model.BaseModel;
import de.twometer.orion.res.ModelLoader;
import de.twometer.orion.util.MathF;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.opengl.GL11.*;

public class ExampleApp extends OrionApp {

    private BaseModel skeld;
    private ExampleShader shader;

    public static void main(String[] args) {
        (new ExampleApp()).launch("Example app", 1024, 768);
    }

    @Override
    public void onInitialize() {
        skeld = ModelLoader.loadModel("TheSkeld.obj");
        shader = getShaderProvider().getShader(ExampleShader.class);
    }

    @Override
    public void onRender() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shader.bind();
        shader.setModelMatrix(new Matrix4f());
        shader.setViewMatrix(getCamera().getViewMatrix());
        shader.setProjMatrix(getCamera().getProjectionMatrix());
        skeld.render();
    }

    @Override
    public void onUpdate(float partial) {
        final float speed = 0.04f;

        float yaw = MathF.toRadians(getCamera().getAngle().x);
        float dx = MathF.sin(yaw) * speed;
        float dz = MathF.cos(yaw) * speed;

        float dx2 = MathF.sin(yaw + MathF.PI / 2) * speed;
        float dz2 = MathF.cos(yaw + MathF.PI / 2) * speed;

        if (getWindow().isKeyPressed(GLFW_KEY_W))
            getCamera().getPosition().add(new Vector3f(dx, 0.0f, dz));

        if (getWindow().isKeyPressed(GLFW_KEY_A))
            getCamera().getPosition().add(new Vector3f(dx2, 0.0f, dz2));

        if (getWindow().isKeyPressed(GLFW_KEY_S))
            getCamera().getPosition().sub(new Vector3f(dx, 0.0f, dz));

        if (getWindow().isKeyPressed(GLFW_KEY_D))
            getCamera().getPosition().sub(new Vector3f(dx2, 0.0f, dz2));

        if (getWindow().isKeyPressed(GLFW_KEY_SPACE))
            getCamera().getPosition().add(new Vector3f(0, speed, 0));

        if (getWindow().isKeyPressed(GLFW_KEY_LEFT_SHIFT))
            getCamera().getPosition().add(new Vector3f(0, -speed, 0));

        Vector2f pos = getWindow().getCursorPosition();
        Vector2f delta = pos.sub(new Vector2f(getWindow().getWidth() / 2.0f, getWindow().getHeight() / 2.0f));
        getCamera().getAngle().add(new Vector2f(-delta.x * 0.04f, -delta.y * 0.04f));
        getWindow().setCursorPosition(new Vector2f(getWindow().getWidth() / 2.0f, getWindow().getHeight() / 2.0f));


        if (getCamera().getAngle().y > 90) getCamera().getAngle().y = 90f;
        if (getCamera().getAngle().y < -90) getCamera().getAngle().y = -90f;
    }
}
