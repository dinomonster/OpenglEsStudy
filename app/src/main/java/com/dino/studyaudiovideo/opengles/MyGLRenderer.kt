package com.dino.studyaudiovideo.opengles

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(context: Context) : GLSurfaceView.Renderer {
    internal val ANGLE_SPAN = 0.375f
    private var mTriangle: Triangle? = null
    private var mContext:Context = context
    internal var mThread: RotateThread? = null
    /**
     * 在View的OpenGL环境被创建的时候调用
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //设置屏幕背景色RGBA
        GLES32.glClearColor(0f, 0f, 0f, 1.0f)
        //创建三角形对象
        mTriangle = Triangle(mContext)
        //打开深度检测
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        //旋转参数线程中进行更改
        mThread = RotateThread()
        mThread?.start()
    }

    /**
     * 每一次View的重绘都会调用
     */
    override fun onDrawFrame(gl: GL10?) {
        mTriangle?.draw()
        //清楚深度缓冲区
        GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
        //三角形的绘制
        mTriangle?.draw()
    }

    /**
     * 如果视图的几何形状发生变化（例如，当设备的屏幕方向改变时），则调用此方法。
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置视窗口大小及位置
        GLES32.glViewport(0, 0, width, height)
        //计算GLSurfaceView的宽高比
        val ration = width.toFloat() / height
        //产生透视投影矩阵
        Matrix.frustumM(Triangle.mProjMatrix, 0, -ration, ration, -1f, 1f, 1f, 10f)
        //设置眼睛的位置
        Matrix.setLookAtM(Triangle.mVMatrix, 0, 0f, 0f, 6f, 0f, 0f, 0f, 0f, 1.0f, 0f)
    }


    //旋转参数在线程中进行更改
    inner class RotateThread : Thread() {
        var flag = true

        override fun run() {
            super.run()
            while (flag) {
                mTriangle?.xAngle = mTriangle!!.xAngle + ANGLE_SPAN

                try {
                    Thread.sleep(20)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }


        }
    }


}