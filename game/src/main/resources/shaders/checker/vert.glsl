layout (location = 0) in vec2 vertex;

uniform mat4 pvm;

out vec2 pos;

void main() {
    gl_Position = pvm * vec4(vertex, 0, 1);
    pos = vertex;
}