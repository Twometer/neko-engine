#version 330 core

layout(location = 0) in vec3 vertexPosition;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main(void) {
    gl_Position = projMatrix * viewMatrix * modelMatrix * vec4(vertexPosition, 1.0);
}