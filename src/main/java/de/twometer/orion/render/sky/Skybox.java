package de.twometer.orion.render.sky;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.gl.Cubemap;
import de.twometer.orion.render.model.ModelBase;
import de.twometer.orion.render.model.Mesh;
import de.twometer.orion.render.shading.IShadingStrategy;
import de.twometer.orion.render.shading.SkyboxShadingStrategy;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class Skybox {

    private boolean active;

    private Cubemap texture;

    private ModelBase cubeModel;

    private final IShadingStrategy strategy = new SkyboxShadingStrategy();

    public void create() {
        float[] skyboxVertices = {
                // positions
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,

                -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,

                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,

                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,

                -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, -1.0f,

                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f
        };
        var mesh = Mesh.create(skyboxVertices.length / 3, 3);
        for (int i = 0; i < skyboxVertices.length; i += 3) {
            mesh.putVertex(skyboxVertices[i], skyboxVertices[i + 1], skyboxVertices[i + 2]);
        }
        cubeModel = mesh.bake("Skybox", GL_TRIANGLES);
        cubeModel.setIgnoreFilters(true);
    }

    public void render() {
        if (!active || texture == null)
            return;

        OrionApp.get().getRenderManager().setShadingStrategy(strategy);
        texture.bind();
        cubeModel.render();
        Cubemap.unbind();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Cubemap getTexture() {
        return texture;
    }

    public void setTexture(Cubemap texture) {
        this.texture = texture;
    }
}
