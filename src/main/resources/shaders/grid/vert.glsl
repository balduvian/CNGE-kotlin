layout (location = 0) in vec3 vertex;

uniform mat4 model;
uniform mat4 pvm;

uniform float width;

out vec2 worldPos;
out vec2 localPos;

void main() {
    gl_Position = pvm * vec4(vertex, 1);

    localPos = vertex.xz * (1 + width);
    worldPos = (model * vec4(vertex, 1)).xz;
}
