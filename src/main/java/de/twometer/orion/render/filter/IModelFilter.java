package de.twometer.orion.render.filter;

import de.twometer.orion.render.model.ModelPart;

public interface IModelFilter {

    boolean shouldRender(ModelPart part);

    void update();

}
