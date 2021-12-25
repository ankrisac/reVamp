#version 430 core

layout(location = 0) uniform vec2 glyph_uv;
layout(location = 1) uniform vec2 offset;
layout(location = 2) uniform ivec2 textbuff_dim;
layout(location = 3) uniform ivec2 textbuff_off;

layout(location = 0) in vec2 pos;
layout(location = 1) in ivec2 cell_uv;
layout(location = 2) in ivec2 cell_id;

layout(location = 0) out vec2 uv;
layout(location = 1) out vec3 color;

layout(std430, binding = 0) buffer TextBuffer {
    ivec2 glyph_offset[];
};


void main() {
    ivec2 cell_pos = (cell_id - textbuff_off) % textbuff_dim;
    int id = cell_pos.x + cell_pos.y * textbuff_dim.x;

    uv = vec2(cell_uv + glyph_offset[id]) * glyph_uv;
    color = vec3(1.0, 1.0, 1.0);

    gl_Position = vec4(pos + offset, 0.0, 1.0);
}