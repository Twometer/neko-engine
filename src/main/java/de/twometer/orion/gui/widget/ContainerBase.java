package de.twometer.orion.gui.widget;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerBase extends WidgetBase {

    private final List<WidgetBase> children = new ArrayList<>();

    @Override
    public void onRelayout() {
        for (var child : children)
            child.onRelayout();
        super.onRelayout();
    }

    public List<WidgetBase> getChildren() {
        return children;
    }

}
