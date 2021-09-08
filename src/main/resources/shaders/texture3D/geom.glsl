#version 460 core

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

uniform vec3 light;
uniform vec3 ambient;
uniform vec3 lightAngle;

in vec2 texCoords[];

out vec2 fragTexCoords;
out vec3 shade;

vec3 getNormal() {
    vec3 a = vec3(gl_in[0].gl_Position) - vec3(gl_in[1].gl_Position);
    vec3 b = vec3(gl_in[2].gl_Position) - vec3(gl_in[1].gl_Position);
    return normalize(cross(a, b));
}

void main() {
    vec3 outShade = mix(ambient, light, max(dot(getNormal(), -normalize(lightAngle)), 0));

    fragTexCoords = texCoords[0];
    shade = outShade;
    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    fragTexCoords = texCoords[1];
    shade = outShade;
    gl_Position = gl_in[1].gl_Position;
    EmitVertex();

    fragTexCoords = texCoords[2];
    shade = outShade;
    gl_Position = gl_in[2].gl_Position;
    EmitVertex();

    EndPrimitive();
}
