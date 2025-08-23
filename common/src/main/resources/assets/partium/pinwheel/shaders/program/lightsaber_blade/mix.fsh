uniform sampler2D DiffuseSampler0;
uniform sampler2D BladeSampler;
uniform sampler2D BladeBlurSampler;
uniform sampler2D HandSampler;
uniform sampler2D HandDepthSampler;
uniform sampler2D MainDepthSampler;
uniform sampler2D ThirdPersonDepthSampler;
uniform sampler2D ThirdPersonSampler;
uniform sampler2D BladeDepthSampler;

uniform int u_IsFirstPerson;

in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

void main() {
    vec4 outColor = texture(DiffuseSampler0, texCoord);
    vec4 bladeCol = texture(BladeSampler, texCoord);
    vec4 bladeBlurCol = texture(BladeBlurSampler, texCoord);
    vec4 handCol  = texture(HandSampler, texCoord);

    float mainDepth  = texture(MainDepthSampler, texCoord).r;
    float bladeDepth = texture(BladeDepthSampler, texCoord).r;
    float handDepth  = texture(HandDepthSampler, texCoord).r;

    float eps = 1e-4;

    if (u_IsFirstPerson == 1) {
        if (bladeDepth + eps < handDepth) {
            //Infront of Hand
            vec3 src = bladeCol.rgb;
            float a = bladeCol.a;
            outColor.rgb = src * a + outColor.rgb * (1.0 - a);
            outColor.a = 1.0;
        } else {
            //Behind Hand
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