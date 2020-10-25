package de.twometer.neko.render.fx;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.gl.Framebuffer;
import de.twometer.neko.render.pipeline.PostRenderer;
import de.twometer.neko.render.shaders.GaussShader;
import de.twometer.neko.render.shading.BloomShadingStrategy;

import static org.lwjgl.opengl.GL11.*;

public class Bloom extends FxBase {

    private final BloomShadingStrategy shadingStrategy = new BloomShadingStrategy();

    private GaussShader gaussShader;

    private Framebuffer buf0;
    private Framebuffer buf1;

    private int texture;

    @Override
    public void create() {
        gaussShader = NekoApp.get().getShaderProvider().getShader(GaussShader.class);
    }

    @Override
    void renderImpl(PostRenderer post) {
        var scene = NekoApp.get().getScene();
        var gBuf = NekoApp.get().getPipeline().getGBuffer();

        glEnable(GL_DEPTH_TEST);
        gBuf.blitDepth(buf0);
        buf0.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        NekoApp.get().getRenderManager().setShadingStrategy(shadingStrategy);
        scene.render();
        Framebuffer.unbind();

        glDisable(GL_DEPTH_TEST);

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
