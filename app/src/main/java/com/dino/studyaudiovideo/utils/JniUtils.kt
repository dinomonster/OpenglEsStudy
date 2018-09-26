package com.example.administrator.studyaudiovideo.utils

object JniUtils {
    init {
        System.loadLibrary("native-lib")
    }

    external fun stringFromJNI(): String
}
