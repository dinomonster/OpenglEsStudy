package com.dino.studyaudiovideo.shape

import android.opengl.GLES32
import android.opengl.Matrix
import android.view.View
import com.dino.studyaudiovideo.base.Shape
import com.dino.studyaudiovideo.utils.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Square(mView: View) : Shape(mView) {
    companion object {
        // 每个顶点的坐标数
        internal val COORDS_PER_VERTEX = 3
        //正方形四个顶点的坐标
        internal var triangleCoords = floatArrayOf(
                -0.5f,  0.5f, 0.0f, // top left
                -0.5f, -0.5f, 0.0f, // bottom left
                0.5f, -0.5f, 0.0f, // bottom right
                0.5f,  0.5f, 0.0f  // top right
        )
        //设置颜色，依次为红绿蓝和透明通道
        internal var color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        // 顶点的绘制顺序
        internal var index = shortArrayOf(0,1,2,0,2,3)
    }
    var mVertexShader = ShaderUtil.loadFromAssetsFile("triangleCamera.glsl", mView.context.resources)//顶点着色器
    var mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.glsl", mView.context.resources)//片元着色器
    var mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader)//自定义渲染管线程序id

    private var mPositionHandle: Int = 0  //顶点位置属性引用id
    private var mColorHandle: Int = 0  //顶点颜色属性引用id
    private var mMVPMatrixHandle : Int = 0  //变换矩阵

    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    //顶点个数
    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    //顶点之间的偏移量
    private val vertexStride = COORDS_PER_VERTEX * 4 // 每个顶点四个字节

    private val mVertexBuffer: FloatBuffer//顶点坐标数据缓冲
    private val mIndexBuffer: ShortBuffer
    init {
        //分配新的直接字节缓冲区
        val bb = ByteBuffer.allocateDirect(
                triangleCoords.size * 4)//float占四个字节
        // 修改ButyBuffer的字节顺序,使用JVM运行的硬件平台的固有字节顺序
        bb.order(ByteOrder.nativeOrder())
        //获取FloatBuffer缓冲
        mVertexBuffer = bb.asFloatBuffer()
        //写入floatArray的顶点坐标
        mVertexBuffer.put(triangleCoords)
        //指向可读数据的首位
        mVertexBuffer.position(0)

        //分配新的直接字节缓冲区
        val cc = ByteBuffer.allocateDirect(index.size * 2)//short占两个字节
        cc.order(ByteOrder.nativeOrder())
        mIndexBuffer = cc.asShortBuffer()
        mIndexBuffer.put(index)
        mIndexBuffer.position(0)
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
        mColorHandle = GLES32.glGetUniformLocation(mProgram, "vColor")
        //设置绘制三角形的颜色
        GLES32.glUniform4fv(mColorHandle, 1, color, 0)

        /**
         * 绘制正方形,使用GL_TRIANGLES模式，由两个三角形拼接
         * mode 绘图模式
         *      GL_TRIANGLES - 这个参数意味着OpenGL使用三个顶点来组成图形。
         *                所以，在开始的三个顶点，将用顶点1，顶点2，顶点3来组成一个三角形。
         *                完成后，在用下一组的三个顶点(顶点4，5，6)来组成三角形，直到数组结束
         * count 依次从索引数组中读取count个顶点来进行绘制
         * type 索引数组中存放的元素的类型
         * indices 指向索引数组的指针
         */
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, index.size, GLES32.GL_UNSIGNED_SHORT, mIndexBuffer)
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
    }

}