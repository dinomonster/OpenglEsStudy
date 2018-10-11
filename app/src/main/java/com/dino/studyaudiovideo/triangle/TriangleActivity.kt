package com.dino.studyaudiovideo.triangle

import android.os.Bundle
import android.view.View
import com.dino.studyaudiovideo.R
import com.dino.studyaudiovideo.base.BaseActivity
import com.dino.studyaudiovideo.utils.ViewUtils
import kotlinx.android.synthetic.main.triangle_activity.*

class TriangleActivity : BaseActivity(), View.OnClickListener {
    override fun setViewId(): Int {
        return R.layout.triangle_activity
    }

    override fun initData() {
        super.initData()
        ViewUtils.setOnClickListeners(this, triangle_btn, triangle_camera_btn, color_triangle_camera_btn)
    }

    override fun onClick(v: View?) {
        when (v) {
            triangle_btn -> {
                var bundle = Bundle()
                bundle.putSerializable("name",Triangle::class.java)
                nextActivity(OpenGLESActivity::class.java,bundle)
            }
            triangle_camera_btn -> {
                nextActivity(OpenGLESActivity::class.java)
            }
            color_triangle_camera_btn -> {
                nextActivity(OpenGLESActivity::class.java)
            }
        }
    }
}
