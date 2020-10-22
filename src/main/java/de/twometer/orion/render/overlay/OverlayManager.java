package de.twometer.orion.render.overlay;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.event.Events;
import de.twometer.orion.event.SizeChangedEvent;
import de.twometer.orion.gl.Framebuffer;
import de.twometer.orion.render.shaders.CopyShader;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class OverlayManager {

    private final List<IOverlay> fx = new ArrayList<>();

    private Framebuffer buf0;
    private Framebuffer buf1;

    private boolean buf = true;

    public void create() {
        Events.register(this);
    }

    public void render() {
        var post = OrionApp.get().getPostRenderer();

        for (IOverlay fx : this.fx) {
            Framebuffer srcBuf = buf ? buf0 : buf1;
            Framebuffer dstBuf = buf ? buf1 : buf0;

            fx.setupShader();
            post.bindTexture(0, srcBuf.getColorTexture());
            post.copyTo(dstBuf);
            buf = !buf;
        }

        Framebuffer.unbind();
        Framebuffer srcBuf = buf ? buf1 : buf0;
        OrionApp.get().getShaderProvider().getShader(CopyShader.class);
        post.bindTexture(0, srcBuf.getColorTexture());
        post.copyTo(null);

        // Reset pingpong
        buf = true;
    }

    @Subscribe
    public void onSizeChanged(SizeChangedEvent e) {
        if (buf0 != null) {
            buf0.destroy();
            buf1.destroy();
        }

        buf0 = Framebuffer.create().withColorTexture(0).finish();
        buf1 = Framebuffer.create().withColorTexture(0).finish();
    }

    public void addOverlay(IOverlay effect) {
        fx.add(effect);
    }

    public boolean isEmtpy() {
        return fx.isEmpty();
    }

    public Framebuffer getBuf0() {
        return buf0;
    }
}
