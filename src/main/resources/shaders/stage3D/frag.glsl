#version 460 core

in vec2 fragTexCoords;
in vec3 shade;
in vec2 pos;

uniform float shadow;

out vec4 color;

void main() {
	float onEdge = min((1 - step(0.05, pos.x)) + step(0.95, pos.x) + (1 - step(0.05, pos.y)) + step(0.95, pos.y), 1);

	color = vec4(shade - (onEdge * 0.1) - ((1 - onEdge) * shadow * 0.75), 1);
}
