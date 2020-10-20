package de.twometer.orion.render;

import de.twometer.orion.render.filter.IModelFilter;
import de.twometer.orion.render.model.ModelPart;

import java.util.ArrayList;
import java.util.List;

public class RenderManager {

    private final List<IModelFilter> modelFilters = new ArrayList<>();

    public void addModelFilter(IModelFilter filter) {
        modelFilters.add(filter);
    }

    public boolean shouldRender(ModelPart part) {
        for (var filter : modelFilters)
            if (!filter.shouldRender(part))
                return false;
        return true;
    }

    public void update() {
        for (var filter : modelFilters)
            filter.update();
    }

}
