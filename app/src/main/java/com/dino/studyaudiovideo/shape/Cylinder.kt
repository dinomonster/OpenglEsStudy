package com.dino.studyaudiovideo.shape

import android.opengl.GLES32
import android.opengl.Matrix
import android.view.View
import com.dino.studyaudiovideo.base.Shape
import com.dino.studyaudiovideo.utils.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Cylinder(mView: View) : Shape(mView) {
    companion object {
        // 每个顶点的坐标数
        internal val COORDS_PER_VERTEX = 3
    }
    private var ovalTop : Oval
    private var ovalBottom : Oval

    private val height = 2.0f  //圆柱体高度
    private val n = 360  //切割份数
    private val radius = 1.0f  //圆半径

    private val mVertexBuffer: FloatBuffer//顶点坐标数据缓冲

    var mVertexShader = ShaderUtil.loadFromAssetsFile("cylinderVertex.glsl", mView.context.resources)//顶点着色器
    var mFragmentShader = ShaderUtil.loadFromAssetsFile("colorfulfrag.glsl", mView.context.resources)//片元着色器
    var mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader)//自定义渲染管线程序id

    private var mPositionHandle: Int = 0  //顶点位置属性引用id
    private var mMVPMatrixHandle: Int = 0  //变换矩阵

    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    private var posSize : Int
    init {
        ovalBottom = Oval(mView)
        ovalTop = Oval(mView,height)
        var data = ArrayList<Float>()
        var angDegSpan = 360f/n
        var i = 0f
        while (i < 360 + angDegSpan) {
            /**
             * 圆点坐标：(x0,y0)
             * 半径：r
             * 角度：angle
             * 圆上任一点为：（x1,y1）
             * x1   =   x0   +   r   *   cos(angle    *   PI   /180   )
             * y1   =   y0   +   r   *   sin(angle    *   PI   /180   )
             */
            data.add((radius * Math.cos(i * Math.PI / 180f)).toFloat())
            data.add((radius * Math.sin(i * Math.PI / 180f)).toFloat())
            data.add(height)
            data.add((radius * Math.cos(i * Math.PI / 180f)).toFloat())
            data.add((radius * Math.sin(i * Math.PI / 180f)).toFloat())
            data.add(0.0f)
            i += angDegSpan
        }
        val f = FloatArray(data.size)
        for (i in f.indices) {
            f[i] = data[i]
        }
        posSize = f.size/3

        //分配新的直接字节缓冲区
        val bb = ByteBuffer.allocateDirect(
                f.size * 4)//float占四个字节
        // 修改ButyBuffer的字节顺序,使用JVM运行的硬件平台的固有字节顺序
        bb.order(ByteOrder.nativeOrder())
        //获取FloatBuffer缓冲
        mVertexBuffer = bb.asFloatBuffer()
        //写入floatArray的顶点坐标
        mVertexBuffer.put(f)
        //指向可读数据的首位
        mVertexBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        //将程序加入到OpenGLES3.2环境
        GLES32.glUseProgram(mProgram)
        // 获取转换矩阵
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        // 将投影和视图转换传递给着色器
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0)
        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition")
        //启用三角形顶点的句柄
        GLES32.glEnableVertexAttribArray(mPositionHandle)
        //准备三角形的坐标数据
        GLES32.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES32.GL_FLOAT, false,
                0, mVertexBuffer)

        /**
         * GL_TRIANGLE_FAN
         * 构建当前三角形的顶点的连接顺序依赖于要和前面已经出现过的2个顶点组成三角形的当前顶点的序号的奇偶性
         * 如果当前顶点是奇数：组成三角形的顶点排列顺序：T = [n-1 n-2 n].
         * 如果当前顶点是偶数：组成三角形的顶点排列顺序：T = [n-2 n-1 n].
         */
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, 0, posSize)
        //禁止顶点数组的句柄
        GLES32.glDisableVertexAttribArray(mPositionHandle)
        ovalBottom.setMatrix(mMVPMatrix)
        ovalBottom.onDrawFrame(gl)
        ovalTop.setMatrix(mMVPMatrix)
        ovalTop.onDrawFrame(gl)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //计算宽高比
        val ratio = width.toFloat() / height
        //透视投影矩阵
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
        // 观察矩阵
        Matrix.setLookAtM(mViewMatrix, 0, 10f, -10f, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // 两个矩阵相乘  变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //开启深度测试
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        ovalBottom.onSurfaceCreated(gl, config)
        ovalTop.onSurfaceCreated(gl, config)
    }
}