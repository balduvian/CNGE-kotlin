#version 460 core

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec3 normVertex;

uniform mat4 pvm;

out vec3 pos;
out vec3 normal;

void main() {
    gl_Position = pvm * vec4(vertex, 1);

    normal = normVertex;
    pos = vertex;
}
