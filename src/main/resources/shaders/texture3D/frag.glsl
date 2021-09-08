#version 460 core

uniform sampler2D sampler;

in vec2 fragTexCoords;
in vec3 shade;

out vec4 outColor;

void main() {
	outColor = texture(sampler, fragTexCoords) * vec4(shade, 1);
}
