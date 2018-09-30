package com.dino.studyaudiovideo.opengles

import android.content.Context
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import com.dino.studyaudiovideo.utils.ShaderUtil

/**
 *
 * mVertexBuffer指的是vertex buffer ,将顶点数据灿在vertex buffer里面后后续的管线操作就可以直接从buffer取的数据，这样加快了数据的读取
 */
class Triangle(context: Context) {
    companion object {
        internal val COORDS_PER_VERTEX = 3
        internal var triangleCoords = floatArrayOf(0.5f, 0.5f, 0.0f, // top
                -0.5f, -0.5f, 0.0f, // bottom left
                0.5f, -0.5f, 0.0f  // bottom right
        )
        //设置颜色，依次为红绿蓝和透明通道
        internal var color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    }

    private var mPositionHandle: Int //顶点位置属性引用id
    private var mColorHandle: Int //顶点颜色属性引用id
    var mVertexShader: String = ShaderUtil.loadFromAssetsFile("vertex.glsl", context.resources)//顶点着色器
    var mFragmentShader: String = ShaderUtil.loadFromAssetsFile("frag.glsl", context.resources)//片元着色器
    var mProgram: Int = ShaderUtil.createProgram(mVertexShader, mFragmentShader)//自定义渲染管线程序id

    var mVertexBuffer: FloatBuffer? = null//顶点坐标数据缓冲

    //顶点个数
    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    //顶点之间的偏移量
    private val vertexStride = COORDS_PER_VERTEX * 4 // 每个顶点四个字节


    init {
        //获取程序中顶点位置属性引用id
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition")
        //获取程序中顶点颜色属性引用id
        mColorHandle = GLES32.glGetAttribLocation(mProgram, "vColor")

        //初始化顶点坐标与着色数据
        initVertexData()
    }


    private fun initVertexData() {
        val bb = ByteBuffer.allocateDirect(
                triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())

        mVertexBuffer = bb.asFloatBuffer()
        mVertexBuffer?.put(triangleCoords)
        mVertexBuffer?.position(0)
    }

    fun draw() {
        //将程序加入到OpenGLES3.2环境
        GLES32.glUseProgram(mProgram)

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
        //绘制三角形
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)
        //禁止顶点数组的句柄
        GLES32.glDisableVertexAttribArray(mPositionHandle)
    }

}