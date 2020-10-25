package example;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.filter.FrustumCullingFilter;
import de.twometer.orion.render.light.LightSource;
import de.twometer.orion.render.model.ModelPart;
import de.twometer.orion.render.overlay.FXAAOverlay;
import de.twometer.orion.render.overlay.VignetteOverlay;
import de.twometer.orion.res.ModelLoader;
import de.twometer.orion.res.TextureLoader;
import de.twometer.orion.util.Log;
import org.joml.Vector3f;

public class ExampleApp extends OrionApp {

    public static void main(String[] args) {
        (new ExampleApp()).launch("Example app", 1280, 720);
    }

    @Override
    public void onInitialize() {
        // Configure window properties
        getWindow().setCursorVisible(false);
        getWindow().setIcon("icon.png");

        // Enable frustum culling
        getRenderManager().addModelFilter(new FrustumCullingFilter());

        // Configuring Post Processing effects
        getFxManager().getSsao().setActive(true);
        getFxManager().getSsao().setSamples(12);
        getFxManager().getBloom().setActive(true);

        // Adding a Vignette and FXAA as overlays
        getOverlayManager().addOverlay(new VignetteOverlay(20.0f, 0.15f));
        getOverlayManager().addOverlay(new FXAAOverlay());

        // Importing a model
        var skeld = ModelLoader.loadModel("TheSkeld.obj");

        // Scanning the model for "Luces" (Lights, my obj file was spanish) and adding light sources there
        skeld.streamTree()
                .filter(m -> m instanceof ModelPart && m.getName().contains("Luces"))
                .forEach(m -> getScene().addLight(new LightSource(m.getCenter())));

        // Adding the model to the scene
        getScene().addModel(skeld);

        // Importing a transformed model
        var astro = ModelLoader.loadModel("Astronaut.obj");
        astro.getTransform().setScale(new Vector3f(0.25f,0.25f,0.25f));
        astro.getTransform().setTranslation(new Vector3f(23,0,-15));
        astro.getTransform().setRotationOrigin(astro.getCenter());
        astro.getTransform().setRotation(new Vector3f(0,-2f,0));
        getScene().addModel(astro);

        // Load the skybox from a cubemap texture
        var skyboxCubemap = TextureLoader.loadCubemap("Sky/right.png", "Sky/left.png", "Sky/top.png", "Sky/bottom.png", "Sky/front.png", "Sky/back.png");
        getScene().getSkybox().setActive(true);
        getScene().getSkybox().setTexture(skyboxCubemap);

        // Show our test "main menu" UI page on screen
        getGuiManager().showPage(new MainPage());

        // Make some noise
        getSoundFX().addAmbiance("ambiancetest.ogg", new Vector3f(0,4,0));
        getSoundFX().play("sfxtest.ogg");

        // I18n
        var testMsg = getI18n().resolve("Test message: {message.test}");
        Log.i(testMsg);
    }

    @Override
    protected void onPreLoad() {
        getGuiManager().setLoadingScreen(new LoadingPage());
    }
}
