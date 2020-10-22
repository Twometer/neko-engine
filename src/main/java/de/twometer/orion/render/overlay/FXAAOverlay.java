package de.twometer.orion.render.overlay;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.shaders.FXAAShader;

public class FXAAOverlay implements IOverlay {

    private final FXAAShader shader;

    public FXAAOverlay() {
        shader = OrionApp.get().getShaderProvider().getShader(FXAAShader.class);
    }

    @Override
    public void setupShader() {
        shader.bind();
    }

}
