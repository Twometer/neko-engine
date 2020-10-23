package de.twometer.orion.render.shading;

import de.twometer.orion.render.model.ModelBasePart;
import de.twometer.orion.res.cache.ShaderProvider;
import de.twometer.orion.res.cache.TextureProvider;

public class NopShadingStrategy implements IShadingStrategy {

    @Override
    public boolean prepareRender(ModelBasePart part, ShaderProvider shaders, TextureProvider textures) {
        // Do nothing
        return true;
    }

}
