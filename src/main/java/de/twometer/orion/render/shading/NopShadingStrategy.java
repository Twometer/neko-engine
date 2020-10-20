package de.twometer.orion.render.shading;

import de.twometer.orion.render.model.ModelPart;
import de.twometer.orion.res.cache.ShaderProvider;
import de.twometer.orion.res.cache.TextureProvider;

public class NopShadingStrategy implements IShadingStrategy {

    @Override
    public void prepareRender(ModelPart part, ShaderProvider shaders, TextureProvider textures) {
        // Do nothing
    }

}
