uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
varying vec4 vColor;
void main(){
    gl_Position = uMVPMatrix * vPosition;
    if(vPosition.z!=0.0){
        vColor=vec4(0.5,0.0,0.0,1.0);
     }else{
        vColor=vec4(0.9,0.9,0.9,1.0);
    }
}