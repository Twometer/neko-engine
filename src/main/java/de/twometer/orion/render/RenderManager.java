package de.twometer.orion.render;

import de.twometer.orion.render.filter.IModelFilter;
import de.twometer.orion.render.model.ModelPart;
import de.twometer.orion.render.shading.IShadingStrategy;
import de.twometer.orion.render.shading.NopShadingStrategy;

import java.util.ArrayList;
import java.util.List;

public class RenderManager {

    private final List<RenderItem> renderItems = new ArrayList<>();

    private final List<IModelFilter> modelFilters = new ArrayList<>();

    private IShadingStrategy shadingStrategy = new NopShadingStrategy();

    public void addModelFilter(IModelFilter filter) {
        modelFilters.add(filter);
    }

    public boolean shouldRender(ModelPart part) {
        for (var filter : modelFilters)
            if (!filter.shouldRender(part))
                return false;
        return true;
    }

    public void addRenderItem(RenderItem renderItem) {
        renderItems.add(renderItem);
    }

    public void update() {
        for (var filter : modelFilters)
            filter.update();
    }

    public void renderFrame() {
        for (var item : renderItems) {
            shadingStrategy = item.getShadingStrategy();
            item.getModel().render();
        }
    }

    public IShadingStrategy getShadingStrategy() {
        return shadingStrategy;
    }
}
