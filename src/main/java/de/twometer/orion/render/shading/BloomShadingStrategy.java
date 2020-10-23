package de.twometer.orion.render.shading;

import de.twometer.orion.render.model.ModelBasePart;
import de.twometer.orion.render.shaders.FlatShader;
import de.twometer.orion.res.cache.ShaderProvider;
import de.twometer.orion.res.cache.TextureProvider;
import org.joml.Matrix4f;

public class BloomShadingStrategy implements IShadingStrategy {

    @Override
    public boolean prepareRender(ModelBasePart part, ShaderProvider shaders, TextureProvider textures) {
        var mat = part.getMaterial();
        if (!mat.isEmissive())
            return false;

        var shader = shaders.getShader(FlatShader.class);
        shader.bind();
        shader.modelMatrix.set(new Matrix4f());
        shader.modelColor.set(mat.getDiffuseColor());
        return true;
    }

}
