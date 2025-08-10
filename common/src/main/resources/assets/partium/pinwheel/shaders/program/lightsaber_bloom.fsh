#include partium:color
#include veil:space_helper

in vec2 texCoord0;

uniform int u_OuterColor;
uniform sampler2D NoiseTexture;
uniform sampler2D DiffuseSampler0;
uniform float GameTime;

out vec4 fragColor;

void main() {
    float offset = 1.0 / 512.0; // adjust to screen resolution
    vec4 sum = texture(DiffuseSampler0, texCoord0) * 0.4;
    sum += texture(DiffuseSampler0, texCoord0 + vec2(offset, 0)) * 0.3;
    sum += texture(DiffuseSampler0, texCoord0 - vec2(offset, 0)) * 0.3;
    fragColor = sum;
}