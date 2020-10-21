package de.twometer.orion.render.fx;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.gl.Framebuffer;
import de.twometer.orion.render.pipeline.PostRenderer;
import de.twometer.orion.render.shaders.GaussShader;
import de.twometer.orion.render.shading.BloomShadingStrategy;

import static org.lwjgl.opengl.GL11.*;

public class Bloom extends FxBase {

    private final BloomShadingStrategy shadingStrategy = new BloomShadingStrategy();

    private GaussShader gaussShader;

    private Framebuffer buf0;
    private Framebuffer buf1;

    private int texture;

    @Override
    public void create() {
        gaussShader = OrionApp.get().getShaderProvider().getShader(GaussShader.class);
    }

    @Override
    void renderImpl(PostRenderer post) {
        var scene = OrionApp.get().getScene();
        var gBuf = OrionApp.get().getPipeline().getGBuffer();

        buf0.bind();
        gBuf.blitDepth(buf0);

        buf0.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        OrionApp.get().getRenderManager().setShadingStrategy(shadingStrategy);
        scene.render();
        Framebuffer.unbind();

        final int passes = 10;
        boolean horizontal = true;
        gaussShader.bind();
        post.begin();
        for (int i = 0; i < passes; i++) {
            var dstBuf = horizontal ? buf1 : buf0;
            var srcBuf = horizontal ? buf0 : buf1;

            gaussShader.horizontal.set(horizontal);

            post.bindTexture(0, srcBuf.getColorTexture());
            post.copyTo(dstBuf);

            horizontal = !horizontal;
        }

        texture = (horizontal ? buf1 : buf0).getColorTexture();

        post.end();
    }

    @Override
    public void resize(int w, int h) {
        if (buf0 != null) {
            buf0.destroy();
            buf1.destroy();
        }

        buf0 = Framebuffer.create()
                .withDepthBuffer()
                .withColorTexture(0)
                .finish();
        buf1 = Framebuffer.create()
                .withColorTexture(0)
                .finish();
    }

    public int getTexture() {
        return texture;
    }

}
