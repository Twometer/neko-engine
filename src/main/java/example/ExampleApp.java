package example;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.filter.FrustumCullingFilter;
import de.twometer.orion.render.light.LightSource;
import de.twometer.orion.render.model.ModelPart;
import de.twometer.orion.render.overlay.FXAAOverlay;
import de.twometer.orion.render.overlay.VignetteOverlay;
import de.twometer.orion.res.ModelLoader;
import de.twometer.orion.res.TextureLoader;
import de.twometer.orion.util.MathF;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class ExampleApp extends OrionApp {

    public static void main(String[] args) {
        (new ExampleApp()).launch("Example app", 1280, 720);
    }

    @Override
    public void onInitialize() {
        getWindow().setCursorVisible(false);

        getRenderManager().addModelFilter(new FrustumCullingFilter());

        getFxManager().getSsao().setActive(true);
        getFxManager().getSsao().setSamples(64);
        getFxManager().getBloom().setActive(true);

        getOverlayManager().addOverlay(new VignetteOverlay(20.0f, 0.15f));
        getOverlayManager().addOverlay(new FXAAOverlay());

        var skeld = ModelLoader.loadModel("TheSkeld.obj");
        skeld.streamTree()
                .filter(m -> m instanceof ModelPart && m.getName().contains("Luces"))
                .forEach(m -> getScene().addLight(new LightSource(m.getCenter())));
        getScene().addModel(skeld);

        var skyboxCubemap = TextureLoader.loadCubemap("Sky/right.png", "Sky/left.png", "Sky/top.png", "Sky/bottom.png", "Sky/front.png", "Sky/back.png");
        getScene().getSkybox().setActive(true);
        getScene().getSkybox().setTexture(skyboxCubemap);

        getGuiManager().showScreen(new ExampleScreen());
    }

    @Override
    public void onTick() {
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
