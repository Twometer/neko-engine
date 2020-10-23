package de.twometer.orion.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class InstanceCache<T> {

    private final Map<Class<T>, T> instances = new HashMap<>();

    public T getInstance(Class<T> clazz) {
        T t = instances.get(clazz);
        if (t == null) {
            try {
                t = Reflect.newInstance(clazz);
                instances.put(clazz, t);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                CrashHandler.fatal(e);
            }
        }
        return t;
    }

}
