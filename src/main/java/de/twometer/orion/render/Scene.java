package de.twometer.orion.render;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.light.LightSource;
import de.twometer.orion.render.model.BaseModel;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private final List<BaseModel> models = new ArrayList<>();

    private final List<LightSource> lights = new ArrayList<>();

    public void render() {
        for (var model : models)
            model.render();
    }

    public void addModel(BaseModel model) {
        models.add(model);
    }

    public void addLight(LightSource light) {
        lights.add(light);
        OrionApp.get().getPipeline().reloadLights();
    }

    public List<LightSource> getLights() {
        return lights;
    }

}
