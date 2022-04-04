layout (location = 0) in vec2 vertex;
layout (location = 1) in float along;

uniform mat4 pvm;

uniform vec4 borderColor;
uniform vec4 insideColor;

out vec4 placeColor;

void main() {
    gl_Position = pvm * vec4(vertex, 0, 1);
    placeColor = mix(borderColor, insideColor, along);
}