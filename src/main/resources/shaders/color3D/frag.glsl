#version 460 core

in vec2 fragTexCoords;
in vec3 shade;

out vec4 color;

void main() {
	color = vec4(shade, 1);
}
