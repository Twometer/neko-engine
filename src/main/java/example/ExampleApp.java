package example;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.filter.FrustumCullingFilter;
import de.twometer.orion.res.ModelLoader;
import de.twometer.orion.util.MathF;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ExampleApp extends OrionApp {

    public static void main(String[] args) {
        (new ExampleApp()).launch("Example app", 1024, 768);
    }

    @Override
    public void onInitialize() {
        getRenderManager().addModelFilter(new FrustumCullingFilter());

        var skeld = ModelLoader.loadModel("TheSkeld.obj");
        addModel(skeld, new ExampleShadingStrategy());
    }

    @Override
    public void onRender() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onUpdate(float partial) {
        final float speed = 0.125f;

        float yaw = MathF.toRadians(getCamera().getAngle().x);
        Vector3f fwd = new Vector3f(MathF.sin(yaw), 0, MathF.cos(yaw)).normalize(speed);
        Vector3f left = new Vector3f(MathF.sin(yaw + MathF.PI / 2), 0, MathF.cos(yaw + MathF.PI / 2)).normalize(speed);

        if (getWindow().isKeyPressed(GLFW_KEY_W))
            getCamera().getPosition().add(fwd);

        if (getWindow().isKeyPressed(GLFW_KEY_A))
            getCamera().getPosition().add(left);

        if (getWindow().isKeyPressed(GLFW_KEY_S))
            getCamera().getPosition().sub(fwd);

        if (getWindow().isKeyPressed(GLFW_KEY_D))
            getCamera().getPosition().sub(left);

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
