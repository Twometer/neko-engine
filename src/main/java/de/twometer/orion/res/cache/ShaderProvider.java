package de.twometer.orion.res.cache;

import de.twometer.orion.gl.Shader;
import de.twometer.orion.util.Log;
import de.twometer.orion.util.Reflect;

import java.lang.reflect.Constructor;
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
                shader = Reflect.newInstance(shaderClass);
                cache.put(shaderClass, shader);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Log.e("Failed to create shader instance", e);
                e.getCause().printStackTrace();
            }
        }
        return (T) shader;
    }



}
