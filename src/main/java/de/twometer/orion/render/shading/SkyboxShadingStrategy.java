package de.twometer.orion.render.shading;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.model.ModelBasePart;
import de.twometer.orion.render.sky.SkyboxShader;
import de.twometer.orion.res.cache.ShaderProvider;
import de.twometer.orion.res.cache.TextureProvider;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class SkyboxShadingStrategy implements IShadingStrategy {

    @Override
    public boolean prepareRender(ModelBasePart part, ShaderProvider shaders, TextureProvider textures) {
        var shader = shaders.getShader(SkyboxShader.class);
        shader.bind();

        Matrix4f viewMatrix = new Matrix4f(new Matrix3f(OrionApp.get().getCamera().getViewMatrix()));
        shader.viewMatrix.set(viewMatrix);

        glDepthFunc(GL_LEQUAL);
        glDepthMask(false);
        return true;
    }

    @Override
    public void finishRender() {
        glDepthFunc(GL_LESS);
        glDepthMask(true);
    }
}
