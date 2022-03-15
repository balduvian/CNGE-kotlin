#version 460 core

in vec2 localPos;
in vec2 worldPos;

uniform float width;
uniform vec4 edgeColor0;
uniform vec4 edgeColor1;
uniform float time;

out vec4 color;

#define PI 3.1415926538

float near(float x, float center, float radius) {
	return sqrt(clamp(1 - abs(x - center) / radius, 0, 1));
}

void main() {
	float onEdge = max(
		near(localPos.x, width / 2, width / 2),
		max(
			near(localPos.y, width / 2, width / 2),
			max(
				near(localPos.x, 1 + width / 2, width / 2),
				near(localPos.y, 1 + width / 2, width / 2)
			)
		)
	);

	vec4 edgeColor = mix(edgeColor0, edgeColor1, (sin(2 * PI * ((worldPos.x + worldPos.y) / 32 + time)) + 1) / 2);

	color = onEdge * edgeColor;
}
