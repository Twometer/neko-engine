package de.twometer.neko.render.fx;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.pipeline.PostRenderer;

public abstract class FxBase {

    private boolean active;

    public final boolean isActive() {
        return active;
    }

    public final void setActive(boolean active) {
        this.active = active;
    }

    public final void render() {
        if (active)
            renderImpl(NekoApp.get().getPostRenderer());
    }

    public abstract void create();

    abstract void renderImpl(PostRenderer post);

    public abstract void resize(int w, int h);

}
