#version 460 core

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec2 texVertex;

uniform mat4 pvm;
uniform vec4 tile;

out vec3 pos;
out vec2 texCoords;

void main() {
    gl_Position = pvm * vec4(vertex, 1);

    texCoords = (texVertex * tile.zw) + tile.xy;

    pos = vertex;
}
