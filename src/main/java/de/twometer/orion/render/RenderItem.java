package de.twometer.orion.render;

import de.twometer.orion.render.model.BaseModel;
import de.twometer.orion.render.shading.IShadingStrategy;

public class RenderItem {

    private final BaseModel model;

    private final IShadingStrategy shadingStrategy;

    public RenderItem(BaseModel model, IShadingStrategy shadingStrategy) {
        this.model = model;
        this.shadingStrategy = shadingStrategy;
    }

    public BaseModel getModel() {
        return model;
    }

    public IShadingStrategy getShadingStrategy() {
        return shadingStrategy;
    }
}
