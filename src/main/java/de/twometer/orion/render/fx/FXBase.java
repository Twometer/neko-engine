package de.twometer.orion.render.fx;

import de.twometer.orion.render.pipeline.PostRenderer;

public abstract class FXBase {

    private boolean active;

    public final boolean isActive() {
        return active;
    }

    public final void setActive(boolean active) {
        this.active = active;
    }

    public void render(PostRenderer post) {
        if (active)
            renderImpl(post);
    }

    public abstract void create();

    abstract void renderImpl(PostRenderer post);

    public abstract void resize(int w, int h);

}
