package de.twometer.orion.render;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.light.LightSource;
import de.twometer.orion.render.model.ModelBase;
import de.twometer.orion.render.sky.Skybox;
import de.twometer.orion.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private boolean initialized = false;

    private final List<ModelBase> models = new ArrayList<>();

    private final List<LightSource> lights = new ArrayList<>();

    private final Skybox skybox = new Skybox();

    public void initialize() {
        if (initialized)
            return;

        // Initialization here
        Log.d("Initializing scene");
        skybox.create();

        initialized = true;
    }

    public void render() {
        for (var model : models)
            model.render();
    }

    public void addModel(ModelBase model) {
        models.add(model);
    }

    public void addLight(LightSource light) {
        lights.add(light);
        OrionApp.get().getPipeline().reloadLights();
    }

    public List<LightSource> getLights() {
        return lights;
    }

    public Skybox getSkybox() {
        return skybox;
    }
}
