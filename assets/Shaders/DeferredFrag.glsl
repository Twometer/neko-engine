#version 330 core

layout(location = 0) out vec4 gPosition;
layout(location = 1) out vec4 gNormal;
layout(location = 2) out vec4 gAlbedoSpec;

in vec3 fragmentPosition;
in vec3 fragmentNormal;
in vec2 fragmentTexture;
in vec4 fragmentColor;

uniform bool hasTexture;
uniform sampler2D texSampler;

void main(void) {
    gPosition = vec4(fragmentPosition, 1.0f);
    gNormal = vec4(normalize(fragmentNormal), 1.0f);
    gAlbedoSpec = hasTexture ? texture(texSampler, fragmentTexture) : fragmentColor;

    if (gAlbedoSpec.a == 0)
        discard;
}