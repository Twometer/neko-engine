package de.twometer.neko.res.cache;

import de.twometer.neko.gl.Shader;
import de.twometer.neko.util.Cache;
import de.twometer.neko.util.Reflect;

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
