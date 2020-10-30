package de.twometer.neko.render.shading;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;

import static org.lwjgl.opengl.GL11.*;

public abstract class AbstractGeometryShadingStrategy implements IShadingStrategy {

    @Override
    public boolean prepareRender(ModelPart part, ShaderProvider shaders, TextureProvider textures) {
        var mat = part.getMaterial();

        if (mat.hasTexture())
            mat.getTexture().bind();

        glDepthMask(!mat.isEmissive() || !NekoApp.get().getFxManager().getBloom().isActive());
        if (!mat.isEmissive() && !mat.isOpaque()) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
        return true;
    }

    @Override
    public void finishRender() {
        glDepthMask(true);
        glDisable(GL_CULL_FACE);
    }
}
