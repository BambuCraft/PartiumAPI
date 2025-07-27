uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec2 ScreenSize;

in vec4 vertexColor;
out vec4 fragColor;

void main() {
    vec2 uv = vec2(1.0 - gl_FragCoord.x + rand(), gl_FragCoord.y) / ScreenSize.xy;
    vec4 color = texture(Sampler0, uv) * ColorModulator;
    fragColor = color;
}