package de.twometer.orion.gui.widget;

import de.twometer.orion.gui.core.BindProperty;
import de.twometer.orion.gui.core.Point;
import de.twometer.orion.gui.core.Size;

public abstract class WidgetBase {

    @BindProperty
    protected String name;

    @BindProperty
    protected Point position;

    @BindProperty
    private Size size;

    @BindProperty
    protected Size margin = new Size(0, 0);

    @BindProperty
    protected Size padding = new Size(0, 0);

    @BindProperty
    protected int tabIndex = 0;

    protected Size internalSize;
    protected Size externalSize;

    protected boolean hasFocus = false;

    // Handlers
    public void onRelayout() {
        var baseSize = size == null ? onComputeSize() : size;
        internalSize = new Size(baseSize.getWidth() + padding.getWidth(), baseSize.getHeight() + padding.getHeight());
        externalSize = new Size(internalSize.getHeight() + margin.getWidth(), internalSize.getHeight() + margin.getHeight());
    }

    public Size onComputeSize() {
        return new Size(0, 0);
    }

    public void onClick(int mx, int my) {
    }

    public void onCharTyped(char c) {
    }

    public void onKeyPress(int k) {
    }

    public void onGotFocus() {
        hasFocus = true;
    }

    public void onLostFocus() {
        hasFocus = false;
    }

    // Accessors
    public Size getInternalSize() {
        return internalSize;
    }

    public Size getExternalSize() {
        return externalSize;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public String getName() {
        return name;
    }
}

