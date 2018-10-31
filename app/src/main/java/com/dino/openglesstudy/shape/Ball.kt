package com.dino.openglesstudy.shape

import android.view.View
import com.dino.openglesstudy.base.Shape
import com.dino.openglesstudy.utils.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.ArrayList
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Ball(mView: View) : Shape(mView) {
    companion object {
        // 每个顶点的坐标数
        internal val COORDS_PER_VERTEX = 3
    }

    private var mVertexShader = ShaderUtil.loadFromAssetsFile("cylinderVertex.glsl", mView.context.resources)//顶点着色器
    private var mFragmentShader = ShaderUtil.loadFromAssetsFile("colorfulfrag.glsl", mView.context.resources)//片元着色器
    private var mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader)//自定义渲染管线程序id

    private var mPositionHandle: Int = 0  //顶点位置属性引用id
    private var mMVPMatrixHandle: Int = 0  //变换矩阵

    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    private val mVertexBuffer: FloatBuffer//顶点坐标数据缓冲

    private val step = 2f
    private val vSize: Int

    init {
        //球以(0,0,0)为中心，以R为半径，则球上任意一点的坐标为
        // ( R * cos(a) * sin(b),y0 = R * sin(a),R * cos(a) * cos(b))
        // 其中，a为圆心到点的线段与xz平面的夹角，b为圆心到点的线段在xz平面的投影与z轴的夹角
        val data = ArrayList<Float>()
        var r1: Float
        var r2: Float
        var h1: Float
        var h2: Float
        var sin: Float
        var cos: Float
        var i = -90f
        while (i < 90 + step) {
            r1 = Math.cos(i * Math.PI / 180.0).toFloat()
            r2 = Math.cos((i + step) * Math.PI / 180.0).toFloat()
            h1 = Math.sin(i * Math.PI / 180.0).toFloat()
            h2 = Math.sin((i + step) * Math.PI / 180.0).toFloat()
            // 固定纬度, 360 度旋转遍历一条纬线
            val step2 = step * 2
            var j = 0.0f
            while (j < 360.0f + step) {
                cos = Math.cos(j * Math.PI / 180.0).toFloat()
                sin = -Math.sin(j * Math.PI / 180.0).toFloat()

                data.add(r2 * cos)
                data.add(h2)
                data.add(r2 * sin)
                data.add(r1 * cos)
                data.add(h1)
                data.add(r1 * sin)
                j += step2
            }
            i += step
        }
        val f = FloatArray(data.size)
        for (i in f.indices) {
            f[i] = data[i]
        }

        val buffer = ByteBuffer.allocateDirect(f.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        mVertexBuffer = buffer.asFloatBuffer()
        mVertexBuffer.put(f)
        mVertexBuffer.position(0)
        vSize = f.size / 3
    }

    override fun onDrawFrame(gl: GL10?) {
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }

}