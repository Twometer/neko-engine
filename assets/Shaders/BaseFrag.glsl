#version 330 core

in vec4 fragmentColor;
in vec2 fragmentTexture;

layout(location = 0) out vec4 color;

uniform bool hasTexture;

uniform sampler2D texSampler;

void main(void) {
    color = hasTexture ? texture(texSampler, fragmentTexture) : fragmentColor;
}