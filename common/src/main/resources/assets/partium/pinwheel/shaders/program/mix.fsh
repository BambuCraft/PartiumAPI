uniform sampler2D DiffuseSampler0; // scene color (backdrop)
uniform sampler2D BladeSampler; // blade color (RGBA)
uniform sampler2D HandSampler; // hand color (RGBA) — optional
uniform sampler2D HandDepthSampler; // hand depth (0..1, NDC depth)
uniform sampler2D MainDepthSampler; // main scene depth (0..1)
uniform sampler2D ThirdPersonDepthSampler; // main scene depth (0..1)
uniform sampler2D ThirdPersonSampler; // main scene depth (0..1)
uniform sampler2D BladeDepthSampler; // blade depth (0..1)

in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

void main() {
    vec4 sceneCol = texture(DiffuseSampler0, texCoord);
    vec4 bladeCol = texture(BladeSampler, texCoord);
    vec4 handCol = texture(HandSampler, texCoord);

    float mainDepth = texture(MainDepthSampler, texCoord).r;
    float bladeDepth =texture(BladeDepthSampler, texCoord).r;
    float handDepth = texture(HandDepthSampler, texCoord).r;

    // Decide which fragment is in front at this pixel:
    // smaller depth = closer to camera (assumes depth in [0,1], 0 = near)
    // Tolerance to avoid z-fighting
    float eps = 1e-4;

    // Start with scene backdrop as base
    vec4 outColor = sceneCol;

    // If blade has alpha > 0, composite blade over backdrop/hand based on depth
    // We'll composite in this order:
    // - If blade is in front of main scene and hand, draw blade over them.
    // - If hand is in front of blade, draw hand over blade.
    // - Otherwise keep scene/backdrop.
    if (bladeCol.a > 0.001) {
        if (handCol.a > 0.001) {
            if (bladeDepth + eps < handDepth) {
                // Blade is closer than hand -> blade appears over hand
                // Standard alpha composite blade over current outColor
                vec3 src = bladeCol.rgb;
                float a = bladeCol.a;
                outColor.rgb = src * a + outColor.rgb * (1.0 - a);
                outColor.a = 1.0;
            } else {
                // Hand is closer -> hand should occlude blade where hand alpha > 0
                // Composite hand over scene (hand over backdrop), but keep blade visible where hand is transparent
                // First draw blade into a temp color (blade over backdrop)
                vec3 bladeOver = bladeCol.rgb * bladeCol.a + outColor.rgb * (1.0 - bladeCol.a);

                // Then composite hand over that result
                float ha = handCol.a;
                outColor.rgb = handCol.rgb * ha + bladeOver * (1.0 - ha);
                outColor.a = 1.0;
            }
        } else {
            // No hand: compare only with main scene (mainDepth typically represents world geometry)
            if (bladeDepth + eps < mainDepth) {
                vec3 src = bladeCol.rgb;
                float a = bladeCol.a;
                outColor.rgb = src * a + outColor.rgb * (1.0 - a);
                outColor.a = 1.0;
            } else {
                // Blade is behind scene geometry; leave scene color
                // (Optionally, you might want a subtle glow behind objects — do nothing here)
            }
        }
    } else {
        // No blade visible; if hand exists, composite hand over scene
        if (bladeDepth + eps < handDepth) {
            float ha = handCol.a;
            outColor.rgb = handCol.rgb * ha + outColor.rgb * (1.0 - ha);
            outColor.a = 1.0;
        }
    }

    fragColor = outColor; //texture(MainDepthSampler, texCoord);
}