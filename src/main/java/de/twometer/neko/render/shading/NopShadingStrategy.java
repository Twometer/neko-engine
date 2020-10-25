package de.twometer.neko.render.shading;

import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;

public class NopShadingStrategy implements IShadingStrategy {

    @Override
    public boolean prepareRender(ModelPart part, ShaderProvider shaders, TextureProvider textures) {
        // Do nothing
        return true;
    }

}
