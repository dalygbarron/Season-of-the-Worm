#version 300 es

in vec4 a_position;
in vec4 a_color;
in vec2 a_texCoord0;

uniform mat4 u_projectionViewMatrix;

out vec2 uv;
out vec4 colour;
out float dist;

void main() {
    gl_Position =  u_projectionViewMatrix * a_position;
    uv = a_texCoord0;
    dist = gl_Position.z;
    colour = a_color;
}