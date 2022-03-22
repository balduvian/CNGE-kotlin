layout (location = 0) in vec2 vertex;

uniform mat4 pvm;
uniform mat4 model;

out vec2 noisePos;
out vec2 vertexPos;

void main() {
	vertexPos = vertex;
	noisePos = (model * vec4(vertex, 0, 1)).xy;
	gl_Position = pvm * vec4(vertex, 0, 1);
}