#version 330 core
out vec4 FragColor;

in vec3 FragPos;
in vec3 Normal;

uniform float fogStr;
uniform vec3 fogColor;

uniform vec3 viewPos;

uniform vec3 color;

struct PointLight {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

#define NR_POINT_LIGHTS 20
uniform PointLight pointLights[NR_POINT_LIGHTS];
uniform int nrPointLights = 0;

struct SpotLight {
    vec3 position;
    vec3 direction;

    vec3 diffuse;
    vec3 specular;

    float cutOff;
    float outerCutOff;
};

#define NR_SPOT_LIGHTS 20
uniform SpotLight spotLights[NR_SPOT_LIGHTS];
uniform int nrSpotLights = 0;

uniform float time;

float shininess = 64.f;

vec3 point_light_influence(PointLight light);
vec3 spot_light_influence(SpotLight light);
float fog_influence();

float near = 0.1; 
float far  = 100.0; 

float LinearizeDepth(float depth) 
{
    float z = depth * 2.0 - 1.0; // back to NDC 
    return (2.0 * near * far) / (far + near - z * (far - near));	
}

void main()
{    
    vec3 color = vec3(0.0, 0.0, 0.0);

    for (int i = 0; i < nrPointLights; i++) {
        color += point_light_influence(pointLights[i]);
    }
    for (int i = 0; i < nrSpotLights; i++) {
        color += spot_light_influence(spotLights[i]);
    }

    // color = mix(color, fogColor, fog_influence());

    FragColor = vec4(color, 1);
}

float fog_influence() {
    float near = 1.f;
    float far = 30.f;
    float dist_to_camera = distance(FragPos, viewPos);
    float influence = min(1.f, dist_to_camera / (far - near));

    return influence * fogStr;
}

vec3 point_light_influence(PointLight light) {
    vec3 normal = normalize(Normal);
    vec3 lightDir   = normalize(light.position - FragPos);
    float diff = max(dot(normal, lightDir), 0.0f);

    float distance    = length(light.position - FragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + 
  			     light.quadratic * (distance * distance));

    vec3 viewDir    = normalize(viewPos - FragPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), shininess);

    vec3 lookVector = normalize(viewPos - FragPos);
    vec3 lightDirReflected = reflect(-lightDir, normal);

    vec3 ambient = light.ambient / nrPointLights * color;
    vec3 diffuse = light.diffuse * diff * color;
    vec3 specular = light.specular * spec * color;

    diffuse *= attenuation;
    specular *= attenuation;

    return ambient + diffuse + specular;
}

vec3 spot_light_influence(SpotLight light) {
    vec3 normal = normalize(Normal);
    vec3 lookingAt = normalize(-light.direction);
    vec3 lightDir = normalize(light.position - FragPos);
    float diff = max(dot(normal, lightDir), 0.0f);

    float f = dot(lookingAt, lightDir);
    float influence = (f - light.outerCutOff) / (light.cutOff - light.outerCutOff);
    influence = max(0.0, min(influence, 1.0));

    vec3 lookVector = normalize(viewPos - FragPos);
    vec3 lightDirReflected = reflect(-lightDir, normal);
    float spec = pow(dot(lightDirReflected, lookVector), shininess);

    vec3 diffuse = light.diffuse * diff * color;
    vec3 specular = light.specular * spec * color;

    diffuse *= influence;
    specular *= influence;

    return diffuse + specular;
}