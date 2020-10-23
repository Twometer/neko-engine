package de.twometer.orion.gui.core;

import de.twometer.orion.gui.widget.WidgetBase;

public class ForeignProperty {

    private final Class<? extends WidgetBase> foreignWidget;

    private final String key;

    private final String value;

    public ForeignProperty(Class<? extends WidgetBase> foreignWidget, String key, String value) {
        this.foreignWidget = foreignWidget;
        this.key = key;
        this.value = value;
    }

    public Class<? extends WidgetBase> getForeignWidget() {
        return foreignWidget;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
