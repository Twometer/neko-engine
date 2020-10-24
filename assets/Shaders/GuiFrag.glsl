#version 330 core

out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D texSampler;

void main(void) {
    FragColor = texture(texSampler, TexCoords);
}