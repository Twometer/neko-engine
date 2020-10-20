#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec2 vertexTexture;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec4 modelColor;

out vec4 fragmentColor;
out vec2 fragmentTexture;

void main(void) {
    gl_Position = projMatrix * viewMatrix * modelMatrix * vec4(vertexPosition, 1.0);
    fragmentColor = modelColor;
    fragmentTexture = vertexTexture;
}