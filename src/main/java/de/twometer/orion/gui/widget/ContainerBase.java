package de.twometer.orion.gui.widget;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerBase extends WidgetBase {

    private final List<WidgetBase> children = new ArrayList<>();

    protected void relayoutChildren() {
        for (var child : children)
            child.onRelayout();
    }

    public List<WidgetBase> getChildren() {
        return children;
    }

    protected WidgetBase singleChild() {
        if (children.size() > 1)
            throw new IllegalStateException(getClass() + " does not allow more than 1 child.");
        return children.get(0);
    }

}
