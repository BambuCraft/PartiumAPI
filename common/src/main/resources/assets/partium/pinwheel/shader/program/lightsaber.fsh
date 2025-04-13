#version 150

varying vec4 fragColor;
varying vec2 fragTexCoord;

uniform sampler2D DiffuseSampler;

void main() {
    vec4 baseColor = texture2D(DiffuseSampler, fragTexCoord) * fragColor;
    vec4 saberColor = vec4(0.0, 1.0, 0.0, 1.0); // Green color

    float distanceFromCenter = length(gl_PointCoord - vec2(0.5, 0.5));
    float glow = exp(-distanceFromCenter * 5.0);
    vec4 glowEffect = saberColor * glow;

    gl_FragColor = mix(baseColor, glowEffect, glowEffect.a);
}
