#version 300 es

uniform sampler2D u_texture;
uniform mediump sampler3D colourMap;

in mediump vec2 uv;
in mediump vec4 colour;
in mediump float dist;
out mediump vec4 FragColor;

void main() {
    FragColor = texture(u_texture, uv) * colour;
    if (FragColor.a < 0.9) discard;
    FragColor *= 0.9 - (dist / 10.0);
    FragColor = texture(colourMap, FragColor.rgb);
}
