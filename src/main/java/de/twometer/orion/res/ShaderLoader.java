package de.twometer.orion.res;

import de.twometer.orion.util.Log;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;

public class ShaderLoader {

    public static int loadShader(String vert, String frag) {
        Log.d("Loading shader " + vert + "; " + frag);

        String vertPath = AssetPaths.SHADER_PATH + vert;
        String fragPath = AssetPaths.SHADER_PATH + frag;

        try {

            int vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
            int fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

            String vertexShaderCode = ResourceLoader.loadString(vertPath);
            String fragmentShaderCode = ResourceLoader.loadString(fragPath);

            glShaderSource(vertexShaderId, vertexShaderCode);
            glCompileShader(vertexShaderId);
            checkShaderError(vertexShaderId);

            glShaderSource(fragmentShaderId, fragmentShaderCode);
            glCompileShader(fragmentShaderId);
            checkShaderError(fragmentShaderId);

            int programId = glCreateProgram();
            glAttachShader(programId, vertexShaderId);
            glAttachShader(programId, fragmentShaderId);
            glLinkProgram(programId);

            // After the shader program is linked, the shader sources can be cleaned up
            glDetachShader(programId, vertexShaderId);
            glDetachShader(programId, fragmentShaderId);
            glDeleteShader(vertexShaderId);
            glDeleteShader(fragmentShaderId);

            return programId;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkShaderError(int shaderId) {
        String log = glGetShaderInfoLog(shaderId);
        if (log.length() > 0) {
            Log.e("Shader compilation failed");
            Log.e(log);
        }
    }

}
