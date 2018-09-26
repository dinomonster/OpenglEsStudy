package com.dino.studyaudiovideo.utils

import android.Manifest.permission_group.LOCATION
import android.content.res.Resources
import android.opengl.GLES32
import android.util.Log
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.Charset

object ShaderUtil {
    /**
     * 加载制定shader的方法
     * @param shaderType shader的类型 GLES32.GL_VERTEX_SHADER(顶点) GLES32.GL_FRAGMENT_SHADER(片元)
     * @param shaderCode shader的脚本字符串
     */
    fun loadShader(shaderType: Int, shaderCode: String): Int {
        // 创建一个新shader
        var shader = GLES32.glCreateShader(shaderType)
        // 若创建成功则加载shader
        if (shader != 0) {
            // 加载shader的源代码
            GLES32.glShaderSource(shader, shaderCode)
            // 编译shader
            GLES32.glCompileShader(shader)
            // 存放编译成功shader数量的数组
            val compiled = IntArray(1)
            // 获取Shader的编译情况
            GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {// 若编译失败则显示错误日志并删除此shader
                Log.e("ES20_ERROR", "Could not compile shader $shaderType:")
                Log.e("ES20_ERROR", GLES32.glGetShaderInfoLog(shader))
                GLES32.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    // 创建shader程序的方法
    fun createProgram(vertexSource: String, fragmentSource: String): Int {
        // 加载顶点着色器
        var vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }

        // 加载片元着色器
        var pixelShader = loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentSource)
        if (pixelShader == 0) {
            return 0
        }

        // 创建程序
        var program = GLES32.glCreateProgram()
        // 若程序创建成功则向程序中加入顶点着色器与片元着色器
        if (program != 0) {
            // 向程序中加入顶点着色器
            GLES32.glAttachShader(program, vertexShader)
            checkGlError("glAttachShader")
            // 向程序中加入片元着色器
            GLES32.glAttachShader(program, pixelShader)
            checkGlError("glAttachShader")
            // 链接程序
            GLES32.glLinkProgram(program)
            // 存放链接成功program数量的数组
            var linkStatus = IntArray(1)
            // 获取program的链接情况
            GLES32.glGetProgramiv(program, GLES32.GL_LINK_STATUS, linkStatus, 0)
            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES32.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ")
                Log.e("ES20_ERROR", GLES32.glGetProgramInfoLog(program))
                GLES32.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    // 检查每一步操作是否有错误的方法
    fun checkGlError(op: String) {
        var error = GLES32.glGetError()
        while (error != GLES32.GL_NO_ERROR) {
            Log.e("ES20_ERROR", "$op: glError $error")
            throw RuntimeException("$op: glError $error")
        }
    }

    // 从sh脚本中加载shader内容的方法
    fun loadFromAssetsFile(fname: String, r: Resources): String {
        return r.assets.open(fname).use {
            it.readBytes().toString(Charset.defaultCharset())
        }
    }

}