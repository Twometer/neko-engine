#version 140

in vec2 position;

out vec2 TexCoords;

void main(void) {
    gl_Position = vec4(position, 0.0, 1.0);
    TexCoords = position * 0.5 + 0.5;
}