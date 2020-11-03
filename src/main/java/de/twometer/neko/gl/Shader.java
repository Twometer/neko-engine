package de.twometer.neko.gl;

import de.twometer.neko.api.Inject;
import de.twometer.neko.api.Dimensions;
import de.twometer.neko.api.Location;
import de.twometer.neko.api.UniformInject;
import de.twometer.neko.core.NekoApp;
import de.twometer.neko.res.ShaderLoader;
import de.twometer.neko.util.Log;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {

    private final int id;

    @SuppressWarnings("rawtypes")
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

    public static void unbind() {
        glUseProgram(0);
    }

    @SuppressWarnings("unchecked")
    private void setDefaults() {
        var win = NekoApp.get().getWindow();
        var cam = NekoApp.get().getCamera();
        for (var u : uniforms) {
            switch (u.getInjectType()) {
                case ProjMatrix:
                    u.set(cam.getProjectionMatrix());
                    break;
                case ViewMatrix:
                    u.set(cam.getViewMatrix());
                    break;
                case ViewportSize:
                    u.set(new Vector2f(win.getWidth(), win.getHeight()));
                    break;
                case CameraPosition:
                    u.set(cam.getPosition());
                    break;
                case None:
                    break;
                default:
                    throw new UnsupportedOperationException("Uniform injection of " + u.getInjectType() + " not implemented.");
            }
        }
    }

    protected void bindSampler(String sampler, int unit) {
        glUniform1i(glGetUniformLocation(id, sampler), unit);
    }

    public void init() {

    }

}
