package de.twometer.neko.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Reflect {

    public static <T> T newInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        var c = clazz.getConstructors();
        if (c.length == 0)
            throw new InstantiationException("Can't find constructor");
        return (T) c[0].newInstance();
    }

    public static Collection<Field> getAllFields(Class<?> clazz) {
        if (clazz == Object.class)
            return Collections.emptySet();

        Set<Field> fields = new HashSet<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        fields.addAll(getAllFields(clazz.getSuperclass()));
        return fields;
    }

}
