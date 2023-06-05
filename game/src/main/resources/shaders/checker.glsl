layout (location = 0) in vec2 vertex;

uniform mat4 pvm;
uniform mat4 model;

out vec2 pos;

void main() {
    gl_Position = pvm * vec4(vertex, 0, 1);
    pos = vertex;
}

#split

uniform vec4 color0;
uniform vec4 color1;

in vec2 pos;

out vec4 outColor;

void main() {
    float xColor = step(0.5, mod(2.0 * pos.x, 1.0));
    float yColor = step(0.5, mod(2.0 * pos.y + 0.5, 1.0));

    float along = xColor * yColor + (1.0 - xColor) * (1.0 - yColor);

    outColor = mix(color0, color1, along);
}
