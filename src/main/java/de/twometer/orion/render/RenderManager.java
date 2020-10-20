package de.twometer.orion.render;

import de.twometer.orion.render.filter.IModelFilter;
import de.twometer.orion.render.light.PointLight;
import de.twometer.orion.render.model.BaseModel;
import de.twometer.orion.render.model.ModelPart;
import de.twometer.orion.render.shading.DeferredShadingStrategy;
import de.twometer.orion.render.shading.IShadingStrategy;

import java.util.ArrayList;
import java.util.List;

public class RenderManager {

    private final List<BaseModel> models = new ArrayList<>();

    private final List<IModelFilter> modelFilters = new ArrayList<>();

    private final List<PointLight> lights = new ArrayList<>();

    private IShadingStrategy shadingStrategy = new DeferredShadingStrategy();

    public void addModelFilter(IModelFilter filter) {
        modelFilters.add(filter);
    }

    public boolean shouldRender(ModelPart part) {
        for (var filter : modelFilters)
            if (!filter.shouldRender(part))
                return false;
        return true;
    }

    public void addModel(BaseModel model) {
        models.add(model);
    }

    public void update() {
        for (var filter : modelFilters)
            filter.update();
    }

    public void renderFrame() {
        for (var model : models) {
            model.render();
        }
    }

    public List<PointLight> getLights() {
        return lights;
    }

    public void addLight(PointLight light) {
        lights.add(light);
    }

    public void setShadingStrategy(IShadingStrategy shadingStrategy) {
        this.shadingStrategy = shadingStrategy;
    }

    public IShadingStrategy getShadingStrategy() {
        return shadingStrategy;
    }
}
