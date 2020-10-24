#version 330 core

layout(location = 0) in vec2 vertexPosition;

out vec2 TexCoords;

uniform mat4 guiMatrix;

void main(void) {
    gl_Position = guiMatrix * vec4(vertexPosition, 0.0f, 1.0f);
    TexCoords = vertexPosition;
}