#version 430 core

in vec2 uv;
in vec3 color;

out vec4 frag_color;

uniform sampler2D fontAtlas;

void main() {
    vec3 glyph_color = texture(fontAtlas, uv).rgb;
    frag_color = vec4(color * glyph_color, 0.5);
}