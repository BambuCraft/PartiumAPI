vec4 i2f(uint c) {
    float r = float((c >> 24) & 0xFFu) / 255.0;
    float g = float((c >> 16) & 0xFFu) / 255.0;
    float b = float((c >>  8) & 0xFFu) / 255.0;
    float a = float((c      ) & 0xFFu) / 255.0;
    return vec4(r, g, b, a);
}