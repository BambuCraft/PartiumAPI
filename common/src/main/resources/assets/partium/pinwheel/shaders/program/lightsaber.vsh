#version 150

layout (location = 0) in vec3 Position;
layout (location = 1) in vec4 Color;
layout (location = 2) in vec2 UV0;
layout (location = 2) in ivec2 UV2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float u_Time;

void main() {
    //mat4 test = ModelViewMat * sin(u_Time);
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}