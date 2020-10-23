package de.twometer.orion.render.shading;

import de.twometer.orion.render.model.ModelBasePart;
import de.twometer.orion.res.cache.ShaderProvider;
import de.twometer.orion.res.cache.TextureProvider;

public interface IShadingStrategy {

    boolean prepareRender(ModelBasePart part, ShaderProvider shaders, TextureProvider textures);

    default void finishRender() {

    }

}
