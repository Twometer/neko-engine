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

    abstract void renderImpl(PostRenderer post);

}
