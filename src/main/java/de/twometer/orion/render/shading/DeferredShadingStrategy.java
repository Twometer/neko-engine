package de.twometer.orion.render.shading;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.model.ModelBasePart;
import de.twometer.orion.render.pipeline.DeferredShader;
import de.twometer.orion.res.cache.ShaderProvider;
import de.twometer.orion.res.cache.TextureProvider;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.glDepthMask;

public class DeferredShadingStrategy implements IShadingStrategy {
    private RenderPass pass;

    @Override
    public boolean prepareRender(ModelBasePart part, ShaderProvider shaders, TextureProvider textures) {
        var shader = shaders.getShader(DeferredShader.class);
        var mat = part.getMaterial();

        if (mat.hasTexture())
            mat.getTexture().bind();

        if ((pass == RenderPass.Opaque && mat.isOpaque()) || (pass == RenderPass.Translucent && !mat.isOpaque()))
            return false;

        glDepthMask(!mat.isEmissive() || !OrionApp.get().getFxManager().getBloom().isActive());

        shader.bind();
        shader.modelColor.set(mat.getDiffuseColor());
        shader.modelMatrix.set(new Matrix4f());
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
