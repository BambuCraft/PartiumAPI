//TODO: I'm ignoring alpha, otherwise it would be completly transparent when using 0xRRGGBB
vec4 i2f(uint c) {
    float a = float((c >> 24) & 0xFFu) / 255.0;
    float r = float((c >> 16) & 0xFFu) / 255.0;
    float g = float((c >>  8) & 0xFFu) / 255.0;
    float b = float((c >>  0) & 0xFFu) / 255.0;
    return vec4(r, g, b, a);
}
