#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform float strength; // 15.0
uniform float exponent; // 0.25
uniform sampler2D texInput;

void main() {
    vec2 uv = TexCoords;
    uv *=  1.0 - uv.yx;
    float vig = uv.x * uv.y * strength;
    vig = min(pow(vig, exponent), 1.0f);

    FragColor = texture(texInput, TexCoords) * vig;
}