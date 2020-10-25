package de.twometer.neko.render.overlay;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.shaders.FXAAShader;

public class FXAAOverlay implements IOverlay {

    private final FXAAShader shader;

    public FXAAOverlay() {
        shader = NekoApp.get().getShaderProvider().getShader(FXAAShader.class);
    }

    @Override
    public void setupShader() {
        shader.bind();
    }

}
