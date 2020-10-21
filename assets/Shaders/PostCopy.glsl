#version 140

out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D sampler;

void main(void){
    FragColor = texture(sampler, TexCoords);
}