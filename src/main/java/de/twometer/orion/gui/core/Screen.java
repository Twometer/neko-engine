package de.twometer.orion.gui.core;

import de.twometer.orion.gui.widget.ContainerBase;
import de.twometer.orion.res.ScreenLoader;

import java.io.IOException;

public abstract class Screen extends ContainerBase {

    private boolean isLoaded = false;

    public final void load() throws IOException {
        if (isLoaded) return;
        isLoaded = true;
        ScreenLoader.load(this);
        onBind();
    }

    protected abstract void onBind();

}
