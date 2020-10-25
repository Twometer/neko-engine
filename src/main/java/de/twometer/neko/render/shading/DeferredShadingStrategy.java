package de.twometer.neko.render.shading;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.render.pipeline.DeferredShader;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;

import static org.lwjgl.opengl.GL11.glDepthMask;

public class DeferredShadingStrategy implements IShadingStrategy {
    private RenderPass pass;

    @Override
    public boolean prepareRender(ModelPart part, ShaderProvider shaders, TextureProvider textures) {
        var shader = shaders.getShader(DeferredShader.class);
        var mat = part.getMaterial();

        if (mat.hasTexture())
            mat.getTexture().bind();

        if ((pass == RenderPass.Opaque && mat.isOpaque()) || (pass == RenderPass.Translucent && !mat.isOpaque()))
            return false;

        glDepthMask(!mat.isEmissive() || !NekoApp.get().getFxManager().getBloom().isActive());

        shader.bind();
        shader.modelColor.set(mat.getDiffuseColor());
        shader.modelMatrix.set(part.getTransform().getMatrix());
        shader.hasTexture.set(mat.hasTexture());

        return true;
    }

    @Override
    public void finishRender() {
        glDepthMask(true);
    }

    public void setPass(RenderPass pass) {
        this.pass = pass;
    }

    public enum RenderPass {
        Opaque,
        Translucent
    }

}
