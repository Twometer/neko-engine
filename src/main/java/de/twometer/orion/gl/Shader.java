package de.twometer.orion.gl;

import de.twometer.orion.api.Inject;
import de.twometer.orion.api.Dimensions;
import de.twometer.orion.api.Location;
import de.twometer.orion.api.UniformInject;
import de.twometer.orion.core.OrionApp;
import de.twometer.orion.res.ShaderLoader;
import de.twometer.orion.util.Log;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {

    private final int id;

    private final List<Uniform> uniforms = new ArrayList<>();

    public Shader(String name) {
        this(name + "Vert.glsl", name + "Frag.glsl");
    }

    public Shader(String vertex, String fragment) {
        id = ShaderLoader.loadShader(vertex, fragment);
        bind();
        bindUniforms();
        init();
        unbind();
    }

    private void bindUniforms() {
        var fields = getClass().getDeclaredFields();
        for (var field : fields) {
            field.setAccessible(true);
            if (field.getType() == Uniform.class) {
                Location loc = field.getAnnotation(Location.class);
                Dimensions dim = field.getAnnotation(Dimensions.class);
                Inject def = field.getAnnotation(Inject.class);

                var locationString = loc != null ? loc.value() : field.getName();

                var dimensions = dim == null ? 0 : dim.value();
                var location = glGetUniformLocation(id, locationString);
                var defaults = def == null ? UniformInject.None : def.value();
                Uniform<?> u = new Uniform<>(location, dimensions, defaults);
                try {
                    field.set(this, u);
                    uniforms.add(u);
                } catch (IllegalAccessException e) {
                    Log.e("Failed to set uniform field", e);
                }
            }
        }
    }

    public final void bind() {
        glUseProgram(id);
        setDefaults();
    }

    public final void unbind() {
        glUseProgram(0);
    }

    private void setDefaults() {
        var cam = OrionApp.get().getCamera();
        for (var u : uniforms) {
            switch (u.getInjectType()) {
                case ProjMatrix -> u.set(cam.getProjectionMatrix());
                case ViewMatrix -> u.set(cam.getViewMatrix());
            }
        }
    }

    protected void bindSampler(String sampler, int unit) {
        glUniform1i(glGetUniformLocation(id, sampler), unit);
    }

    public void init() {

    }

}
