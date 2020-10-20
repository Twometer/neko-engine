#version 140

out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

const int MAX_NR_LIGHTS = 128;

uniform vec3 viewPos;
uniform vec3 lights[MAX_NR_LIGHTS];
uniform int numLights;

void main(void){
    // retrieve data from gbuffer
    vec3 FragPos = texture(gPosition, TexCoords).rgb;
    vec3 Normal = texture(gNormal, TexCoords).rgb;
    vec3 Diffuse = texture(gAlbedoSpec, TexCoords).rgb;
    float Specular = texture(gAlbedoSpec, TexCoords).a;

    const float linear = 1.0f;
    const float quadratic = 1.0f;
    const vec3 color = vec3(1, 1, 1);

    // then calculate lighting as usual
    vec3 lighting  = Diffuse * 0.1;// hard-coded ambient component
    vec3 viewDir  = normalize(viewPos - FragPos);
    for (int i = 0; i < numLights; ++i)
    {
        // diffuse
        vec3 lightDir = normalize(lights[i] - FragPos);
        vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Diffuse * color;
        // specular
        vec3 halfwayDir = normalize(lightDir + viewDir);
        float spec = pow(max(dot(Normal, halfwayDir), 0.0), 16.0);
        vec3 specular = color * spec * Specular;
        // attenuation
        float distance = length(lights[i] - FragPos);
        float attenuation = 1.0 / (1.0 + linear * distance + quadratic * distance * distance);
        diffuse *= attenuation;
        specular *= attenuation;
        lighting += diffuse + specular;
    }
    FragColor = vec4(lighting, 1.0);
}