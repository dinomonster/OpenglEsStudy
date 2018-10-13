package com.dino.openglesstudy.shape

import android.opengl.GLES32
import android.view.View
import com.dino.openglesstudy.base.Shape
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class ShapeGLRenderer(mView: View) : Shape(mView) {
    private var shape: Shape? = null
    private var clazz: Class<out Shape> = Triangle::class.java

    fun setShape(shape: Class<out Shape>) {
        this.clazz = shape
    }
    /**
     * 在View的OpenGL环境被创建的时候调用
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //设置屏幕背景色RGBA
        GLES32.glClearColor(0f, 0f, 0f, 1.0f)
        try {
            val constructor = clazz.getDeclaredConstructor(View::class.java)
            constructor.isAccessible = true
            shape = constructor.newInstance(mView) as Shape
        } catch (e: Exception) {
            e.printStackTrace()
            shape = Triangle(mView)
        }

        shape?.onSurfaceCreated(gl, config)
    }

    /**
     * 每一次View的重绘都会调用
     */
    override fun onDrawFrame(gl: GL10?) {
        GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
        shape?.onDrawFrame(gl)
    }

    /**
     * 如果视图的几何形状发生变化（例如，当设备的屏幕方向改变时），则调用此方法。
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
        shape?.onSurfaceChanged(gl, width, height)
    }


}