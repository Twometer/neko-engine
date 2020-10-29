package de.twometer.neko.render.overlay;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.event.Events;
import de.twometer.neko.event.SizeChangedEvent;
import de.twometer.neko.gl.Framebuffer;
import de.twometer.neko.render.shaders.CopyShader;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class OverlayManager {

    private final List<IOverlay> overlays = new ArrayList<>();

    private Framebuffer buf0;
    private Framebuffer buf1;

    private boolean buf = true;

    public void create() {
        Events.register(this);
    }

    public void render() {
        var post = NekoApp.get().getPostRenderer();

        for (IOverlay fx : this.overlays) {
            Framebuffer srcBuf = buf ? buf0 : buf1;
            Framebuffer dstBuf = buf ? buf1 : buf0;

            fx.setupShader();
            post.bindTexture(0, srcBuf.getColorTexture());
            post.copyTo(dstBuf);
            buf = !buf;
        }

        Framebuffer.unbind();
        Framebuffer srcBuf = buf ? buf1 : buf0;
        NekoApp.get().getShaderProvider().getShader(CopyShader.class);
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

    public void removeOverlay(Class<? extends IOverlay> overlay) {
        overlays.removeIf(o -> o.getClass() == overlay);
    }

    public void addOverlay(IOverlay effect) {
        overlays.add(effect);
    }

    public boolean isEmtpy() {
        return overlays.isEmpty();
    }

    public Framebuffer getBuf0() {
        return buf0;
    }
}
