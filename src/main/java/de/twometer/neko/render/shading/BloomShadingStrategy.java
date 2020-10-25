package de.twometer.neko.render.shading;

import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.render.shaders.FlatShader;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;
import org.joml.Matrix4f;

public class BloomShadingStrategy implements IShadingStrategy {

    @Override
    public boolean prepareRender(ModelPart part, ShaderProvider shaders, TextureProvider textures) {
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
