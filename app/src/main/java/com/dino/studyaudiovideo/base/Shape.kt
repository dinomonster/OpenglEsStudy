package com.dino.studyaudiovideo.base

import android.opengl.GLES20
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.view.View
import java.nio.FloatBuffer

/**
 * Description:
 */
abstract class Shape(protected var mView: View) : GLSurfaceView.Renderer