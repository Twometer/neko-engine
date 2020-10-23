package de.twometer.orion.gui.core;

import de.twometer.orion.gui.widget.WidgetBase;

public class ForeignProperty {

    public static ForeignProperty EMPTY = new ForeignProperty(null, null, null);

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

    public int intValue(int defaultVal) {
        return value == null ? defaultVal : Integer.parseInt(value);
    }
}
