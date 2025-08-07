//#include partium:color

in vec2 texCoord0;

uniform uint u_InnerColor;
uniform uint u_OuterColor;
uniform float u_Time;

out vec4 fragColor;

//TODO: I'm ignoring alpha, otherwise it would be completly transparent when using 0xRRGGBB
vec4 i2f(uint c) {
    float a = float((c >> 24) & 0xFFu) / 255.0;
    float r = float((c >> 16) & 0xFFu) / 255.0;
    float g = float((c >>  8) & 0xFFu) / 255.0;
    float b = float((c >>  0) & 0xFFu) / 255.0;
    return vec4(r, g, b, a);
}

void main() {
    float glowIntensity = 2.0;
    float glowWidth = 1.0;
    float alpha = pow(smoothstep(0.0, 1.0, 0.2 * glowWidth),1.5);
    if (alpha < 0.01) {
        discard;
    }
    vec3 color = i2f(u_OuterColor).rgb * glowIntensity;
    fragColor = vec4(color, alpha);
}
