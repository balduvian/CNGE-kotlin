in vec2 localPos;
in vec2 worldPos;

uniform float width;
uniform vec4 edgeColor0;
uniform vec4 edgeColor1;
uniform vec4 centerColor;
uniform float time;

out vec4 color;

#define PI 3.1415926538

void main() {
	float onEdge = min(
		(1 - step(width, localPos.x)) +
		step(1, localPos.x) +
		(1 - step(width, localPos.y)) +
		step(1, localPos.y),
		1
	);

	vec4 edgeColor = mix(edgeColor0, edgeColor1, (sin(2 * PI * ((worldPos.x + worldPos.y) / 32 + time)) + 1) / 2);

	color = onEdge * edgeColor + (1 - onEdge) * centerColor;
}
