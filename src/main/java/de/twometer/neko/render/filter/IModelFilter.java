package de.twometer.neko.render.filter;

import de.twometer.neko.render.model.ModelPart;

public interface IModelFilter {

    boolean shouldRender(ModelPart part);

    void update();

}
