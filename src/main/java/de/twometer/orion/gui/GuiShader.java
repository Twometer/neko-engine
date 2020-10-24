package de.twometer.orion.gui;

import de.twometer.orion.gl.Shader;
import de.twometer.orion.gl.Uniform;
import org.joml.Matrix4f;

public class GuiShader extends Shader {

    public Uniform<Matrix4f> guiMatrix;

    public GuiShader() {
        super("GuiVert.glsl", "GuiFrag.glsl");
    }

}
