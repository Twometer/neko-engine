package de.twometer.orion.res.cache;

import de.twometer.orion.gl.Shader;
import de.twometer.orion.util.Cache;
import de.twometer.orion.util.Log;
import de.twometer.orion.util.Reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ShaderProvider extends Cache<Class<? extends Shader>, Shader> {

    @Override
    protected Shader create(Class<? extends Shader> aClass) throws Exception {
        return Reflect.newInstance(aClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends Shader> T getShader(Class<T> clazz) {
        return (T) get(clazz);
    }

}
