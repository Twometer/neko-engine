package de.twometer.neko.render.shading;

import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.render.pipeline.GeometryShader;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;

import static org.lwjgl.opengl.GL11.*;

public class DefaultGeometryShadingStrategy extends AbstractGeometryShadingStrategy {

    @Override
    public boolean prepareRender(ModelPart part, ShaderProvider shaders, TextureProvider textures) {
        super.prepareRender(part, shaders, textures);
        var mat = part.getMaterial();

        var shader = shaders.getShader(GeometryShader.class);
        shader.bind();
        shader.modelColor.set(mat.getDiffuseColor());
        shader.modelMatrix.set(part.getTransform().getMatrix());
        shader.hasTexture.set(mat.hasTexture());

        return true;
    }


}
