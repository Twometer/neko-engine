#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec2 vertexTexture;

uniform vec4 modelColor;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

out vec3 fragmentPosition;
out vec3 fragmentNormal;
out vec2 fragmentTexture;
out vec4 fragmentColor;

void main(void) {
    vec4 worldPos = modelMatrix * vec4(vertexPosition, 1.0);
    fragmentPosition = worldPos.xyz;
    gl_Position = projMatrix * viewMatrix * worldPos;

    mat3 normalMatrix = transpose(inverse(mat3(modelMatrix)));
    fragmentNormal = normalMatrix * vertexNormal;

    fragmentTexture = vertexTexture;
    fragmentColor = modelColor;
}