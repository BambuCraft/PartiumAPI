#include partium:color
#include veil:space_helper

uniform sampler2D DiffuseSampler0;
uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    fragColor = texture(DiffuseSampler0, texCoord);
}