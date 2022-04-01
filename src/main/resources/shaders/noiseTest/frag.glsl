#include </shaders/noise.glsl>

uniform vec4 color;

in vec2 noisePos;
in vec2 vertexPos;

out vec4 outColor;

void main() {
	float alpha = step(
		0.0,
		gnoise(vec4(noisePos, 12.0, -32.0))
	);

	outColor = vec4(color.xyz, color.w * alpha);
}