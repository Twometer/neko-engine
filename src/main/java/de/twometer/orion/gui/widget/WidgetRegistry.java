package de.twometer.orion.gui.widget;

import de.twometer.orion.util.CrashHandler;
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

    public static Class<? extends WidgetBase> getWidget(String name) {
        var clazz = registry.get(name);

        if (clazz == null)
            throw new NoSuchElementException("Widget " + name + " is not registered.");

        return clazz;
    }

    public static WidgetBase createWidget(String name) {
        var clazz = getWidget(name);

        try {
            return Reflect.newInstance(clazz);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            CrashHandler.fatal(e);
            return null;
        }
    }

}
