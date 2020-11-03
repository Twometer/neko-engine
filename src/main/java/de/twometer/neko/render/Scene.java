package de.twometer.neko.render;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.light.LightSource;
import de.twometer.neko.render.model.ModelBase;
import de.twometer.neko.render.sky.Skybox;
import de.twometer.neko.util.Log;

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

    public void removeModel(ModelBase model) {
        models.remove(model);
    }

    public void addModel(ModelBase model) {
        models.add(model);
    }

    public void addLight(LightSource light) {
        lights.add(light);
    }

    public List<LightSource> getLights() {
        return lights;
    }

    public Skybox getSkybox() {
        return skybox;
    }
}
