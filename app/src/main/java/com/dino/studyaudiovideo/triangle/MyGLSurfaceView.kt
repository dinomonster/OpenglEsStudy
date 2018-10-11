package com.dino.studyaudiovideo.triangle

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.dino.studyaudiovideo.base.Shape

class MyGLSurfaceView(context: Context, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    private val mRenderer: MyGLRenderer

    init {
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3)

        mRenderer = MyGLRenderer(this)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer)
        // Render the view only when there is a change in the drawing data
//        this.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    fun setShape(clazz: Class<out Shape>) {
        try {
            mRenderer.setShape(clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}