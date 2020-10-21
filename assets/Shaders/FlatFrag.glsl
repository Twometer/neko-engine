#version 330 core

out vec4 FragColor;

uniform vec4 modelColor;

void main(void) {
    FragColor = modelColor;
}