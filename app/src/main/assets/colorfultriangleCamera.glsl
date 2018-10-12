uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
varying vec4 vColor;
attribute vec4 aColor;
void main(){
    gl_Position = uMVPMatrix * vPosition;
    vColor = aColor;
}