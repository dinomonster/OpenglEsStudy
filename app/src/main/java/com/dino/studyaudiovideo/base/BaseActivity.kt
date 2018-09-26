package com.dino.studyaudiovideo.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

abstract class BaseActivity : AppCompatActivity() {
    abstract fun setViewId(): Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (view != null) {
            setContentView(view)
        }
        initData()
    }
    open fun initData(){}

    protected val view: View?
        get() = if (setViewId() != 0) {
            layoutInflater.inflate(setViewId(), null)
        } else {
            null
        }


    fun nextActivity(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }

    fun nextActivity(cls: Class<*>,bundle:Bundle) {
        startActivity(Intent(this, cls).apply { putExtras(bundle) })
    }

}