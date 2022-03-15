#version 460 core

uniform vec3 color0;
uniform vec3 color1;
uniform vec3 color2;

in vec3 pos;

out vec4 color;

void main() {
	float along = (normalize(pos).y + 1) / 2;

	float secondPart = step(0.5, along);
	float firstPart = 1 - secondPart;

	color = vec4(firstPart * mix(color0, color1, along * 2) + secondPart * mix(color1, color2, (along - 0.5) * 2), 1);
}
