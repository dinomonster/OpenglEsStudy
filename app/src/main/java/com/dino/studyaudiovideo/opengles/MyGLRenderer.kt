package com.dino.studyaudiovideo.opengles

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(context: Context) : GLSurfaceView.Renderer {
    private var mTriangle: Triangle? = null
    private var mContext:Context = context
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
    }

    /**
     * 每一次View的重绘都会调用
     */
    override fun onDrawFrame(gl: GL10?) {
        GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
        mTriangle?.draw()
    }

    /**
     * 如果视图的几何形状发生变化（例如，当设备的屏幕方向改变时），则调用此方法。
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
    }


}