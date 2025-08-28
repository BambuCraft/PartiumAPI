uniform sampler2D DiffuseSampler0;
uniform sampler2D ;

in vec2 texCoord;
out vec4 fragColor;

void main(){

    vec4 bladeBlurCol = texture(DiffuseSampler0, texCoord);
}