#version 460 core

uniform vec3 light;
uniform vec3 ambient;
uniform vec3 lightAngle;

in vec3 pos;
in vec3 normal;

out vec4 color;

float rand(vec3 co) {
	return fract(sin(dot(co, vec3(12.9898, 78.233, 139.2907))) * 43758.5453);
}

void main() {
	color = vec4(mix(
		ambient,
		light,
		max(dot(normalize(normal + vec3(rand(pos.xyz), rand(pos.yzx), rand(pos.zxy)) * 0.1), -lightAngle), 0)
	), 1);
}
