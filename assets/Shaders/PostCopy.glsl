#version 140

out vec4 out_Colour;

in vec2 TexCoords;

uniform sampler2D sampler;

void main(void){
    out_Colour = texture(sampler, textureCoords);
}