#version 140

out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D noise;

uniform vec3 samples[64];
uniform vec2 viewportSize;
uniform mat4 projection;
uniform mat4 view;
uniform int kernelSize;

const float radius = 0.5;
const float bias = 0.025;

vec3 samplePositionTex(vec2 coords) {
    return (view * texture(gPosition, coords)).xyz;
}

void main(void){
    vec2 noiseScale = viewportSize / 4.0;

    // get input for SSAO algorithm
    mat3 normalMatrix = transpose(inverse(mat3(view)));

    vec3 fragPos = samplePositionTex(TexCoords);
    vec3 normal = normalize(normalMatrix * texture(gNormal, TexCoords).xyz);
    vec3 randomVec = normalize(texture(noise, TexCoords * noiseScale).xyz);
    // create TBN change-of-basis matrix: from tangent-space to view-space
    vec3 tangent = normalize(randomVec - normal * dot(randomVec, normal));
    vec3 bitangent = cross(normal, tangent);
    mat3 TBN = mat3(tangent, bitangent, normal);
    // iterate over the sample kernel and calculate occlusion factor
    float occlusion = 0.0;
    for (int i = 0; i < kernelSize; ++i)
    {
        // get sample position
        vec3 samplePos = TBN * samples[i];// from tangent to view-space
        samplePos = fragPos + samplePos * radius;

        // project sample position (to sample texture) (to get position on screen/texture)
        vec4 offset = vec4(samplePos, 1.0);
        offset = projection * offset;// from view to clip-space
        offset.xyz /= offset.w;// perspective divide
        offset.xyz = offset.xyz * 0.5 + 0.5;// transform to range 0.0 - 1.0

        // get sample depth
        float sampleDepth = samplePositionTex(offset.xy).z;// get depth value of kernel sample

        // range check & accumulate
        float rangeCheck = smoothstep(0.0, 1.0, radius / abs(fragPos.z - sampleDepth));
        occlusion += (sampleDepth >= samplePos.z + bias ? 1.0 : 0.0) * rangeCheck;
    }
    occlusion = 1.0 - (occlusion / kernelSize);

    FragColor.r = occlusion;
    FragColor.a = 1.0;
}