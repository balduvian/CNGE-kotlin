uniform vec3 color0;
uniform vec3 color1;
uniform vec3 color2;

in vec2 pos;

out vec4 color;

void main() {
	float secondPart = step(0.5, pos.y);
	float firstPart = 1 - secondPart;

	color = vec4(firstPart * mix(color0, color1, pos.y * 2) + secondPart * mix(color1, color2, (pos.y - 0.5) * 2), 1);
}
