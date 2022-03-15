#version 460 core

uniform vec3 color;

in float shade;

out vec4 outColor;

void main() {
	outColor = vec4(color * shade, 1);
}
