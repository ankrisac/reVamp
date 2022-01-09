#version 430 core

uniform sampler2D fontAtlas;

layout(location = 0) in vec2 uv;
layout(location = 1) in vec4 back;
layout(location = 2) in vec4 fore;

layout(location = 0) out vec4 frag_color;

void main() {
    vec4 glyph_color = texture(fontAtlas, uv);
    frag_color = mix(back, fore, glyph_color);
}