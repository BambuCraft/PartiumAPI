
uniform sampler2D DiffuseSampler0;
uniform sampler2D BladeSampler;

 in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

void main(){
    vec4 backdrop = texture(DiffuseSampler0, texCoord);
    vec4 blade = texture(BladeSampler, texCoord);
    float outAlpha = blade.a + backdrop.a * (1.0 - blade.a);
    vec3 outColor = (blade.rgb * blade.a + backdrop.rgb * backdrop.a * (1.0 - blade.a)) / max(outAlpha, 1e-6);
    fragColor = vec4(outColor, outAlpha);
}