#version 460 core

uniform float index;
uniform float time;

uniform vec4 color;

out vec4 outColor;

#define PI 3.1415926538

float rand(vec2 co) {
	return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

float interp(float start, float end, float along) {
	return (end - start) * along + start;
}

void main() {
	float cycleLength = interp(1, 8, rand(vec2(index, 3274.2782)));

	float brightness = sin((time + index) * (2 * PI) / cycleLength);

	outColor = color * vec4(1, 1, 1, brightness);
}
