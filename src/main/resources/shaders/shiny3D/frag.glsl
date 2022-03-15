#version 460 core

in vec3 pos;
in float shade;

uniform float time;

out vec4 color;

#define BASE_COLOR0 vec3(0.70, 1.00, 0.85)
#define BASE_COLOR1 vec3(0.40, 0.67, 1.00)

#define COLOR0 vec3(0.70, 1.00, 0.00)
#define COLOR1 vec3(0.90, 0.00, 0.00)

#define C_LOW 0.1
#define C_HIGH 0.4

float sinRand(float s1, float s2, float s3, float t, float g) {
	return (sin(s1 * t) + sin(s2 * g) + sin(s3 * t)) / 6 + 0.5;
}

float inRange(float low, float high, float x) {
	return step(low, x) * step(x, high);
}

void main() {
	float distance = sinRand(4.5, 9.3, 13.4, pos.x + time, pos.y - time * 0.45) *
		sinRand(2.5, 17.3, 20.2, pos.y + time * 0.75, pos.z - time) *
		sinRand(4.5, 8.5, 10.2, pos.z - time, pos.x + time * 0.82);

	vec3 ccc = (step(distance, C_LOW) * mix(BASE_COLOR0, BASE_COLOR1, smoothstep(0.0, C_LOW, distance))) +

		(inRange(C_LOW, C_HIGH, distance) * mix(COLOR0, COLOR1, smoothstep(C_LOW, C_HIGH, distance))) +

		(step(C_HIGH, distance) * mix(BASE_COLOR1, BASE_COLOR0, smoothstep(C_HIGH, 1.0, distance)));

	color = vec4(ccc + (shade * 0.003), 1);
}
