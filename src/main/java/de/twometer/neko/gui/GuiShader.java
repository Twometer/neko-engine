package de.twometer.neko.gui;

import de.twometer.neko.gl.Shader;
import de.twometer.neko.gl.Uniform;
import org.joml.Matrix4f;

public class GuiShader extends Shader {

    public Uniform<Matrix4f> guiMatrix;

    public GuiShader() {
        super("GuiVert.glsl", "GuiFrag.glsl");
    }

}
