layout (location = 0) in vec2 vertex;

uniform mat4 pvm;

uniform vec4 color0;
uniform vec4 color1;

out vec4 vertColor;

void main() {
    gl_Position = pvm * vec4(vertex, 0, 1);

    vertColor = mix(color0, color1, vertex.y);
}
