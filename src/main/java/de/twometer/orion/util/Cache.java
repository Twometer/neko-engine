package de.twometer.orion.util;

import java.util.HashMap;
import java.util.Map;

public abstract class Cache<ID, T> {

    private final Map<ID, T> cache = new HashMap<>();

    protected abstract T create(ID id) throws Exception;

    public T get(ID id) {
        var result = cache.get(id);
        if (result == null) {
            try {
                result = create(id);
            } catch (Exception e) {
                CrashHandler.fatal(e);
                return null;
            }
            cache.put(id, result);
        }
        return result;
    }

}
