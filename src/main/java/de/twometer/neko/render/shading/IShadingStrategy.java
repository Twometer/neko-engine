package de.twometer.neko.render.shading;

import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;

public interface IShadingStrategy {

    IShadingStrategy NOP = new NopShadingStrategy();

    boolean prepareRender(ModelPart part, ShaderProvider shaders, TextureProvider textures);

    default boolean mayOverwrite() {
        return true;
    }

    default void finishRender() {

    }

}
