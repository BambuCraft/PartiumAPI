#version 150

attribute vec4 Position;
attribute vec4 Color;
attribute vec2 TexCoord0;
uniform mat4 MVPMatrix;

varying vec4 fragColor;
varying vec2 fragTexCoord;

void main() {
    fragColor = Color;
    fragTexCoord = TexCoord0;
    gl_Position = MVPMatrix * Position;
}