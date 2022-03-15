#version 460 core

in vec2 pos;

uniform vec4 ringColor;
uniform float along;

out vec4 color;

#define PI 3.1415926538

float inRadius(vec2 pos, float radius) {
	return 1 - step(radius * radius, (pos.x) * (pos.x) + (pos.y) * (pos.y));
}

float posMod(float a, float b) {
	return mod(b + mod(a, b), b);
}

void main() {
	float angle = posMod(atan(pos.y, pos.x) + PI / 2, PI * 2);

	color = ringColor * vec4(1, 1, 1, inRadius(pos, 0.5) * (1 - inRadius(pos, 0.4)) * step(along * 2 * PI, angle));
}
