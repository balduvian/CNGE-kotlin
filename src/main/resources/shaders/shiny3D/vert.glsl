#version 460 core

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec2 texVertex;
layout (location = 2) in vec3 normVertex;

uniform mat4 pvm;

uniform vec3 lightAngle;

out float shade;
out vec3 pos;

void main() {
    gl_Position = pvm * vec4(vertex, 1);

    shade = max(dot(normVertex, -lightAngle), 0);

    pos = vertex;
}
