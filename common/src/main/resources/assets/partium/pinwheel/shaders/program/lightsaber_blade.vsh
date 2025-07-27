layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec2 texCoord;
out vec4 vertexColor;
out float vertexDistance;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexColor = Color;
    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);
}
