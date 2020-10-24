package example;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.filter.FrustumCullingFilter;
import de.twometer.orion.render.light.LightSource;
import de.twometer.orion.render.model.ModelBasePart;
import de.twometer.orion.render.overlay.FXAAOverlay;
import de.twometer.orion.render.overlay.VignetteOverlay;
import de.twometer.orion.res.ModelLoader;
import de.twometer.orion.res.TextureLoader;

public class ExampleApp extends OrionApp {

    public static void main(String[] args) {
        (new ExampleApp()).launch("Example app", 1280, 720);
    }

    @Override
    public void onInitialize() {
        getWindow().setCursorVisible(false);

        getRenderManager().addModelFilter(new FrustumCullingFilter());

        getFxManager().getSsao().setActive(true);
        getFxManager().getSsao().setSamples(12);
        getFxManager().getBloom().setActive(true);

        getOverlayManager().addOverlay(new VignetteOverlay(20.0f, 0.15f));
        getOverlayManager().addOverlay(new FXAAOverlay());

        var skeld = ModelLoader.loadModel("TheSkeld.obj");
        skeld.streamTree()
                .filter(m -> m instanceof ModelBasePart && m.getName().contains("Luces"))
                .forEach(m -> getScene().addLight(new LightSource(m.getCenter())));
        getScene().addModel(skeld);

        var skyboxCubemap = TextureLoader.loadCubemap("Sky/right.png", "Sky/left.png", "Sky/top.png", "Sky/bottom.png", "Sky/front.png", "Sky/back.png");
        getScene().getSkybox().setActive(true);
        getScene().getSkybox().setTexture(skyboxCubemap);

        getGuiManager().showPage(new MainPage());
    }


}
