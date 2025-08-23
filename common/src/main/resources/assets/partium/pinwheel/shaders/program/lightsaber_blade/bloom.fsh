#include partium:color
#include veil:space_helper

uniform sampler2D DiffuseSampler0;
uniform sampler2D LightSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    int Radius = 5;
    vec2 BlurDir = vec2(0,1);
    vec2 oneTexel = vec2(0,1);

    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;
    for(float r = -Radius; r <= Radius; r += 1.0) {
        float strength = abs(1.0 - r / Radius);
        strength = strength * strength;
        totalStrength = totalStrength + strength;
        blurred = blurred + texture2D(DiffuseSampler0, texCoord + oneTexel * r * BlurDir) * strength;
    }
    fragColor = vec4(blurred.rgb / totalStrength, texture2D(DiffuseSampler0, texCoord).a);
}
uniform sampler2D DiffuseSampler0; // original blade (alpha = blade)
uniform sampler2D DiffuseSampler1; // blurred blade (RGB)
uniform vec2 OutSize;               // screen size in px (optional)
uniform float GlowStrength;         // overall glow multiplier, e.g. 1.0
uniform float GlowSpread;           // how wide the glow is (0..1), e.g. 0.6
uniform vec3 GlowColor;             // tint color (e.g. vec3(0.0, 0.6, 1.0))

in vec2 texCoord;
out vec4 fragColor;

float linearStep(float edge0, float edge1, float x) {
    return clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
}

void main() {
    // base samples
    vec4 base = texture(DiffuseSampler0, texCoord);    // original blade (RGBA)
    vec3 blur = texture(DiffuseSampler1, texCoord).rgb; // blurred RGB

    // Use alpha of base to find how close we are to blade center.
    // alpha = 1.0 on blade, 0.0 elsewhere. We want glow strongest near blade and fade out.
    float a = base.a;

    // Compute a "distance factor" from the blade by sampling neighbor alpha values.
    // This helps create a soft falloff near edges (0..1, 0=on blade center, 1=far).
    // We sample a few offsets to estimate local alpha spread quickly.
    float spread = GlowSpread; // control radius
    vec2 px = 1.0 / OutSize;
    float edgeSum = 0.0;
    edgeSum += texture(DiffuseSampler0, texCoord + vec2(px.x * 1.0, 0.0)).a;
    edgeSum += texture(DiffuseSampler0, texCoord - vec2(px.x * 1.0, 0.0)).a;
    edgeSum += texture(DiffuseSampler0, texCoord + vec2(0.0, px.y * 1.0)).a;
    edgeSum += texture(DiffuseSampler0, texCoord - vec2(0.0, px.y * 1.0)).a;
    edgeSum *= 0.25; // average neighbor alpha

    // If alpha is near 1, we are in blade -> keep core bright; if alpha is near 0 but neighbors have alpha,
    // we are in the immediate glow area.
    float near = max(a, edgeSum);

    // distanceFactor: 0 at blade center (near==1), 1 at far (near==0)
    float distanceFactor = 1.0 - near;

    // Apply a smooth falloff curve for nicer look
    float falloff = pow(distanceFactor, 1.5); // tweak exponent for softer/harder falloff

    // Combine blurred color with tint and strength.
    // Multiply blur by tint to color the glow, then scale by falloff and global strength.
    vec3 glow = blur * GlowColor * (GlowStrength * falloff);

    // Composite mode: additive for glow over background, then alpha-blend original blade on top.
    // final RGB = background + glow + base.rgb * base.alpha
    // We assume destination/background is black (full-screen pass). If not, use blending when drawing.
    vec3 finalRGB = glow + base.rgb * base.a;

    // compute final alpha: keep blade alpha (so later passes or blending can use it)
    float finalA = max(base.a, clamp(GlowStrength * (1.0 - distanceFactor) * 0.6, 0.0, 1.0));

    fragColor = vec4(finalRGB, finalA);
}