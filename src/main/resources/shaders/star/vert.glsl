#version 460 core

layout (location = 0) in vec2 vertex;
layout (location = 1) in float vertexShade;

uniform mat4 pvm;

out float shade;

void main() {
    gl_Position = pvm * vec4(vertex, 0, 1);
    shade = vertexShade;
}
