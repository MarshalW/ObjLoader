uniform mat4 uProjectionM;
attribute vec3 aPosition;
void main() {
    gl_Position = uProjectionM * vec4(aPosition, 1.0);
}