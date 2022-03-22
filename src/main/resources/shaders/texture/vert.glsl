layout (location = 0) in vec2 vertex;
layout (location = 1) in vec2 texVertex;

uniform mat4 pvm;

out vec2 texCoords;

void main() {
    gl_Position = pvm * vec4(vertex, 0, 1);

    texCoords = texVertex;
}
