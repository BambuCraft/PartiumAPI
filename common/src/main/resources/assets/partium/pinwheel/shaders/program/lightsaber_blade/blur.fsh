uniform sampler2D DiffuseSampler0;
uniform vec2 OutSize;

in vec2 texCoord;
out vec4 fragColor;

const float offset = 3;

void main() {
    vec2 dir = vec2(1.0/OutSize.x, 0.0);
    vec3 result = texture(DiffuseSampler0, texCoord).rgb * 0.2;

    for (int i = 1; i <= 15; ++i) {
        vec2 off = float(i) * dir;
        vec3 a = texture(DiffuseSampler0, texCoord + off).rgb;
        vec3 b = texture(DiffuseSampler0, texCoord - off).rgb;
        result += (a + b) * 0.4;
    }
    fragColor = vec4(result, 1.0);
}

/*void main() {
    vec2 halfpixel = 0.5 / OutSize;

    vec4 sum = texture(DiffuseSampler0, texCoord) * 4.0;
    sum += texture(DiffuseSampler0, texCoord - halfpixel.xy * offset);
    sum += texture(DiffuseSampler0, texCoord + halfpixel.xy * offset);
    sum += texture(DiffuseSampler0, texCoord + vec2(halfpixel.x, -halfpixel.y) * offset);
    sum += texture(DiffuseSampler0, texCoord - vec2(halfpixel.x, -halfpixel.y) * offset);
    fragColor = sum / 8.0;
}*/
