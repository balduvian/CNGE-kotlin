uniform vec4 color0;
uniform vec4 color1;

in vec2 pos;

out vec4 color;

void main() {
	color = mix(color0, color1, pos.x * pos.x);
}