package de.twometer.neko.render.sky;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.gl.Cubemap;
import de.twometer.neko.render.model.ModelBase;
import de.twometer.neko.render.model.Mesh;
import de.twometer.neko.render.shading.IShadingStrategy;
import de.twometer.neko.render.shading.SkyboxShadingStrategy;

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

        NekoApp.get().getRenderManager().setShadingStrategy(strategy);
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
