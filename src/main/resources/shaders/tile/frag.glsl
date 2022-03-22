uniform sampler2D sampler;
uniform vec4 color;

in vec2 texCoords;
out vec4 outColor;

void main() {
	outColor = texture(sampler, texCoords) * color;
}
