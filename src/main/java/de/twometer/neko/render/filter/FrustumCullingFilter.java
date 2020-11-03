package de.twometer.neko.render.filter;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.model.ModelPart;

public class FrustumCullingFilter implements IModelFilter {


    @Override
    public boolean shouldRender(ModelPart part) {
        return NekoApp.get().getFrustumCulling().insideFrustum(part.getTransformedCenter(), part.getSize().length());
    }

    @Override
    public void update() {

    }


}
