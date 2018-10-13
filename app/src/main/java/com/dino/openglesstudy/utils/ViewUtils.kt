package com.dino.openglesstudy.utils

import android.view.View
import android.widget.TextView

object ViewUtils {

    /**
     * 设置多个view的是否可用属性
     */
    fun setIsEnable(boolean: Boolean, vararg view: View) {
        for (v in view) {
            v.isEnabled = boolean
        }
    }

    /**
     * 设置多个view的是否可见属性
     */
    fun setIsVisibility(int: Int, vararg view: View) {
        for (v in view) {
            v.visibility = int
        }
    }

    /**
     * 设置多个view的hint属性
     */
    fun setTextHint(replaceText: String, vararg view: TextView) {
        for (v in view) {
            v.hint = v.hint.toString().format(replaceText)
        }
    }

    /**
     * 设置多个view的linster
     */
    fun setOnClickListeners(listener: View.OnClickListener, vararg view: View) {
        for (v in view) {
            v.setOnClickListener(listener)
        }
    }

}