package de.twometer.orion.render.shading;

import de.twometer.orion.render.model.ModelPart;
import de.twometer.orion.render.pipeline.DeferredShader;
import de.twometer.orion.res.cache.ShaderProvider;
import de.twometer.orion.res.cache.TextureProvider;
import org.joml.Matrix4f;

public class DeferredShadingStrategy implements IShadingStrategy {
    @Override
    public boolean prepareRender(ModelPart part, ShaderProvider shaders, TextureProvider textures) {
        var shader = shaders.getShader(DeferredShader.class);
        var mat = part.getMaterial();

        if (mat.hasTexture())
            mat.getTexture().bind();

        shader.bind();
        shader.modelColor.set(mat.getDiffuseColor());
        shader.modelMatrix.set(new Matrix4f());
        shader.hasTexture.set(mat.hasTexture());

        return true;
    }
}
