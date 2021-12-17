#version 420 core

in vec2 uv;
out vec4 frag_color;

uniform sampler2D testTexture;

void main() {
    //vec3 color = texture(testTexture, uv).rgb;
    vec3 color = vec3(1.0, 0.0, 0.0);
    frag_color = vec4(color, 1.0);
}