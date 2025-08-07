//#include veil:space_helper

uniform sampler2D DiffuseSampler0;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 camPos = VeilCamera.CameraPosition + VeilCamera.CameraBobOffset;
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    fragColor = baseColor;
}