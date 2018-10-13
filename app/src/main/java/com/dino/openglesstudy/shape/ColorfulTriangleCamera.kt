package com.dino.openglesstudy.shape

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import android.opengl.GLES32
import android.opengl.Matrix
import android.view.View
import com.dino.openglesstudy.base.Shape
import com.dino.openglesstudy.utils.ShaderUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 * mVertexBuffer指的是vertex buffer ,将顶点数据灿在vertex buffer里面后后续的管线操作就可以直接从buffer取的数据，这样加快了数据的读取
 */
class ColorfulTriangleCamera(mView: View) : Shape(mView) {
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    companion object {
        internal val COORDS_PER_VERTEX = 3
        internal var triangleCoords = floatArrayOf(
                0.0f,  0.622008459f, 0.0f, // top
                -0.5f, -0.311004243f, 0.0f, // bottom left
                0.5f, -0.311004243f, 0.0f  // bottom right
        )
        internal var color = floatArrayOf(
                0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        )
    }

    var mVertexShader = ShaderUtil.loadFromAssetsFile("colorfultriangleCamera.glsl", mView.context.resources)//顶点着色器
    var mFragmentShader = ShaderUtil.loadFromAssetsFile("colorfulfrag.glsl", mView.context.resources)//片元着色器
    var mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader)//自定义渲染管线程序id
    private var mPositionHandle: Int = 0  //顶点位置属性引用id
    private var mColorHandle: Int = 0  //顶点颜色属性引用id
    private var mMVPMatrixHandle : Int = 0  //变换矩阵

    var mVertexBuffer: FloatBuffer//顶点坐标数据缓冲
    var mColorBuffer: FloatBuffer//颜色数据缓冲

    //顶点个数
    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    //顶点之间的偏移量
    private val vertexStride = COORDS_PER_VERTEX * 4 // 每个顶点四个字节


    init {
        //初始化顶点坐标与着色数据
        val bb = ByteBuffer.allocateDirect(
                triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())

        mVertexBuffer = bb.asFloatBuffer()
        mVertexBuffer.put(triangleCoords)
        mVertexBuffer.position(0)

        val dd = ByteBuffer.allocateDirect(
                color.size * 4)
        dd.order(ByteOrder.nativeOrder())
        mColorBuffer = dd.asFloatBuffer()
        mColorBuffer.put(color)
        mColorBuffer.position(0)
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
                vertexStride, mVertexBuffer)
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES32.glGetAttribLocation(mProgram, "aColor")
        //设置绘制三角形的颜色
        GLES32.glEnableVertexAttribArray(mColorHandle)
        GLES32.glVertexAttribPointer(mColorHandle, 4, GLES32.GL_FLOAT, false, 0, mColorBuffer)

        //绘制三角形
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)
        //禁止顶点数组的句柄
        GLES32.glDisableVertexAttribArray(mPositionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //计算宽高比
        val ratio = width.toFloat() / height
        //透视投影矩阵
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        // 观察矩阵
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // 两个矩阵相乘  变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //开启深度测试
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
    }


}