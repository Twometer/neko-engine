package de.twometer.orion.render.filter;

import de.twometer.orion.render.model.ModelBasePart;

public interface IModelFilter {

    boolean shouldRender(ModelBasePart part);

    void update();

}
