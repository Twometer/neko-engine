package de.twometer.orion.gui.core;

import de.twometer.orion.core.OrionApp;
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

    @Override
    public void onRelayout() {
        super.onRelayout();
        var child = singleChild();
        child.setPosition(new Point(getPadding().getWidth(), getPadding().getHeight()));
        child.setMaximumSize(getInternalSize());
    }

    @Override
    public Size onComputeSize() {
        var win = OrionApp.get().getWindow();
        return new Size(win.getWidth(), win.getHeight());
    }

    protected abstract void onBind();

}
