package de.twometer.orion.gui.widget;

import de.twometer.orion.gui.widget.Button;
import de.twometer.orion.gui.widget.Grid;
import de.twometer.orion.gui.widget.WidgetBase;
import de.twometer.orion.util.Reflect;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class WidgetRegistry {

    private static final Map<String, Class<? extends WidgetBase>> registry = new HashMap<>();

    static {
        register(Button.class);
        register(Grid.class);
    }

    public static void register(Class<? extends WidgetBase> widget) {
        registry.put(widget.getSimpleName(), widget);
    }

    public static WidgetBase createWidget(String name) {
        var clazz = registry.get(name);

        if (clazz == null)
            throw new NoSuchElementException("Widget " + name + " is not registered.");

        try {
            return Reflect.newInstance(clazz);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
