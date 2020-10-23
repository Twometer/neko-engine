package de.twometer.orion.gui.widget;

import de.twometer.orion.gui.core.*;

import java.util.ArrayList;
import java.util.List;

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

    @BindProperty
    protected Alignment verticalAlignment = Alignment.Fill;

    @BindProperty
    protected Alignment horizontalAlignment = Alignment.Fill;

    protected Size internalSize;
    protected Size externalSize;
    protected Size maximumSize;

    protected boolean hasFocus = false;

    private final List<ForeignProperty> foreignProperties = new ArrayList<>();

    // Handlers
    public void onRelayout() {
        var baseSize = size == null ? computeSize() : size;
        internalSize = new Size(baseSize.getWidth() + padding.getWidth(), baseSize.getHeight() + padding.getHeight());
        externalSize = new Size(internalSize.getWidth() + margin.getWidth(), internalSize.getHeight() + margin.getHeight());
    }

    private Size computeSize() {
        var size = onComputeSize();
        if (verticalAlignment == Alignment.Fill)
            size.setHeight(maximumSize.getHeight());
        if (horizontalAlignment == Alignment.Fill)
            size.setWidth(maximumSize.getWidth());
        return size;
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

    public List<ForeignProperty> getForeignProperties() {
        return foreignProperties;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Size getMargin() {
        return margin;
    }

    public Size getPadding() {
        return padding;
    }

    public Size getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(Size maximumSize) {
        this.maximumSize = maximumSize;
    }

}

