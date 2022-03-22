layout (location = 0) in vec2 vertex;

uniform mat4 pvm;

void main() {
    gl_Position = pvm * vec4(vertex, 0, 1);
}