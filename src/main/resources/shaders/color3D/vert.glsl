#version 460 core

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec2 texVertex;
layout (location = 2) in vec3 normVertex;

uniform mat4 pvm;

uniform vec3 light;
uniform vec3 ambient;
uniform vec3 lightAngle;

out vec2 texCoords;
out vec3 shade;

void main() {
    gl_Position = pvm * vec4(vertex, 1);

    shade = mix(ambient, light, max(dot(normVertex, -lightAngle), 0));

    texCoords = texVertex;
}
