uniform sampler2D DiffuseSampler0; // scene color (backdrop)
uniform sampler2D BladeSampler; // blade color (RGBA)
uniform sampler2D HandSampler; // hand color (RGBA) — optional
uniform sampler2D HandDepthSampler; // hand depth (0..1, NDC depth)
uniform sampler2D MainDepthSampler; // main scene depth (0..1)
uniform sampler2D ThirdPersonDepthSampler; // main scene depth (0..1)
uniform sampler2D ThirdPersonSampler; // main scene depth (0..1)
uniform sampler2D BladeDepthSampler; // blade depth (0..1)

uniform int u_IsFirstPerson;

in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

void main() {
    vec4 sceneCol = texture(DiffuseSampler0, texCoord);
    vec4 bladeCol = texture(BladeSampler, texCoord);
    vec4 handCol = texture(HandSampler, texCoord);

    float mainDepth = texture(MainDepthSampler, texCoord).r;
    float bladeDepth =texture(BladeDepthSampler, texCoord).r;
    float handDepth = texture(HandDepthSampler, texCoord).r;

    float eps = 1e-4;

    vec4 outColor = sceneCol;

    if (u_IsFirstPerson == 1) {
        if (bladeDepth + eps < handDepth) {
            //Infront of Hand
            vec3 src = bladeCol.rgb;
            float a = bladeCol.a;
            outColor.rgb = src * a + outColor.rgb * (1.0 - a);
            outColor.a = 1.0;
        } else {
            //Behind of Hand
            vec3 bladeOver = bladeCol.rgb * bladeCol.a + outColor.rgb * (1.0 - bladeCol.a);
            float ha = handCol.a;
            outColor.rgb = handCol.rgb * ha + bladeOver * (1.0 - ha);
            outColor.a = 1.0;
        }
    } else {
        if (bladeDepth + eps < mainDepth) {
            vec3 src = bladeCol.rgb;
            float a = bladeCol.a;
            outColor.rgb = src * a + outColor.rgb * (1.0 - a);
            outColor.a = 1.0;
        }
    }
    fragColor = outColor;
}