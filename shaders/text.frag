#version 430 core



uniform sampler2D fontAtlas;

layout(location = 0) in vec2 uv;
layout(location = 1) in vec3 color;

layout(location = 0) out vec4 frag_color;


void main() {
    vec3 glyph_color = texture(fontAtlas, uv).rgb;

    frag_color = vec4(color * glyph_color, 0.5);
}