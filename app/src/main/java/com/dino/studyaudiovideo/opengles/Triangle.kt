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
 * 四个重要的矩阵mMMatrix，mVMatrix，mProjMatrix，mMVPMatrix
 * mMMatrix指的是物体本身位置，角度的状态矩阵
 * mVMatrix指的是人眼看物体时候的位置，角度的状态矩阵
 * mProjMatrix指的是物体成像所处的透视环境状态
 * mMVPMatrix指的是mMMatrix,mVMatrix,mProjMatrix三者结合的矩阵
 *
 * mVertexBuffer指的是vertex buffer ,将顶点数据灿在vertex buffer里面后后续的管线操作就可以直接从buffer取的数据，这样加快了数据的读取
 * mColorBuffer 类似
 */
class Triangle(context: Context) {
    companion object {
        internal val mProjMatrix = FloatArray(16)//4x4矩阵 投影用
        internal val mVMatrix = FloatArray(16)//摄像机位置朝向9参数矩阵
        internal val mMMatrix = FloatArray(16)//具体物体的移动旋转矩阵，旋转、平移
    }

    private var muMVPMatrixHandle: Int//总变换矩阵引用id
    private var maPositionHandle: Int //顶点位置属性引用id
    private var maColorHandle: Int //顶点颜色属性引用id
    var mVertexShader: String = ShaderUtil.loadFromAssetsFile("vertex.glsl", context.resources)//顶点着色器
    var mFragmentShader: String = ShaderUtil.loadFromAssetsFile("frag.glsl", context.resources)//片元着色器
    var mProgram: Int = ShaderUtil.createProgram(mVertexShader, mFragmentShader)//自定义渲染管线程序id

    var mVertexBuffer: FloatBuffer? = null//顶点坐标数据缓冲
    var mColorBuffer: FloatBuffer? = null//顶点着色数据缓冲
    var vCount = 3
    var xAngle = 0f//绕x轴旋转的角度

    init {
        //获取程序中顶点位置属性引用id
        maPositionHandle = GLES32.glGetAttribLocation(mProgram, "aPosition")
        //获取程序中顶点颜色属性引用id
        maColorHandle = GLES32.glGetAttribLocation(mProgram, "aColor")
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")

        //初始化顶点坐标与着色数据
        initVertexData()
    }


    private fun initVertexData() {
        //顶点坐标数据的初始化
        var unitSize = 0.2f
        var vertices = floatArrayOf(
                0.0f,  0.622008459f, 0.0f, // top
                -0.5f, -0.311004243f, 0.0f, // bottom left
                0.5f, -0.311004243f, 0.0f  // bottom right
        )//顶点的坐标的

        var vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        mVertexBuffer = vbb.asFloatBuffer()
        mVertexBuffer?.put(vertices)
        mVertexBuffer?.position(0)

        var colors = floatArrayOf(
                1f, 0f, 0f, 1f,
                0f, 0f, 1f, 0f,
                0f, 1f, 0f, 0f
        )//三个顶点的颜色值采用的是RGBA

        var cbb = ByteBuffer.allocateDirect(colors.size * 4)
        cbb.order(ByteOrder.nativeOrder())
        mColorBuffer = cbb.asFloatBuffer()
        mColorBuffer?.put(colors)
        mColorBuffer?.position(0)
    }

    var i = 0
    fun draw() {
        //制定使用某套shader程序
        GLES32.glUseProgram(mProgram)
        //初始化变换矩阵
        Matrix.setRotateM(mMMatrix, 0, 0f, 0f, 1f, 0f)//是将mMMatrix初始化为单位矩阵
        i = 1
        if (i == 1) {
            for (j in 0 until mMMatrix.size) {
                Log.i("liang.chen", "item$j is:${mMMatrix[j]}")
            }
        }
        i++


        //设置沿Z轴正向位移1
        Matrix.translateM(mMMatrix, 0, 0f, 0f, -1f)
        //设置绕x轴旋转
        Matrix.rotateM(mMMatrix, 0, xAngle, 0f, 0f, 1f)
        //
        GLES32.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, getFianlMatrix(mMMatrix), 0)
        //为画笔指定顶点位置数据
        /**
         * glVertexAttribPointer和glVertexAttribPointer作用是对顶点属性值进行赋值
         * 以第一个glVertexAttribPointer为例
         * maPositionHandle是vertex shader中对应的位置属性值
         * 3指的是坐标点是以顶点数组中三个值为一个顶点
         * GLES20.GL_FLOAT是顶点数组中数据类型
         * 34值的是Buffer中一个顶点的大小因为三个float组成一个顶点所以是34
         * mVertexBuffer就是顶点坐标数组转成的buffer了
         */
        GLES32.glVertexAttribPointer(
                maPositionHandle,
                3,
                GLES32.GL_FLOAT,
                false,
                3 * 4,
                mVertexBuffer
        )
        GLES32.glVertexAttribPointer(
                maColorHandle,
                4,
                GLES32.GL_FLOAT,
                false,
                4 * 4,
                mColorBuffer
        )
        //允许顶点位置数据数组
        GLES32.glEnableVertexAttribArray(maPositionHandle)
        GLES32.glEnableVertexAttribArray(maColorHandle)
        //绘制三角形
        //Android的openlgles的图元有点，线，三角形，所有的图形就靠它们来组成，因为这里
        //是直接画三角形，所以就用GLES20.GL_TRAINGLES
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vCount)

    }

    private fun getFianlMatrix(spec: FloatArray): FloatArray {
        var mMVPMatrix = FloatArray(16)//最后起作用的总变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0)
        return mMVPMatrix
    }
}