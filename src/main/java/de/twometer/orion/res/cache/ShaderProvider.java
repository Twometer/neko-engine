package de.twometer.orion.res.cache;

import de.twometer.orion.gl.Shader;
import de.twometer.orion.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ShaderProvider {

    private final Map<Class<? extends Shader>, Shader> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Shader> T getShader(Class<T> shaderClass) {
        Shader shader = cache.get(shaderClass);
        if (shader == null) {
            try {
                shader = newInstance(shaderClass);
                cache.put(shaderClass, shader);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Log.e("Failed to create shader instance", e);
                e.getCause().printStackTrace();
            }
        }
        return (T) shader;
    }

    private static <T> T newInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        var c = clazz.getConstructors();
        if (c.length == 0)
            throw new InstantiationException("Can't find constructor");
        return (T) c[0].newInstance();
    }

}
