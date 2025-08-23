#include partium:color
#include veil:light

in vec2 texCoord0;

uniform int u_InnerColor;
uniform float u_Time;

out vec4 fragColor;

void main() {
    fragColor = i2f(u_InnerColor);
}
