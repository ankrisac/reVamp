#version 430 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec4 col_back;
layout(location = 3) in vec4 col_fore;

layout(location = 0) out vec2 out_uv;
layout(location = 1) out vec4 out_col_back;
layout(location = 2) out vec4 out_col_fore;

void main() {
    out_col_back = col_back;
    out_col_fore = col_fore;

    out_uv = uv;
    gl_Position = vec4(pos.xy, -pos.z, 1.0);
}