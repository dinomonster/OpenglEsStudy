package com.dino.openglesstudy.shape

import com.dino.openglesstudy.R
import com.dino.openglesstudy.base.BaseActivity
import com.dino.openglesstudy.base.Shape
import kotlinx.android.synthetic.main.opengles_activity.*

class ShapeActivity : BaseActivity() {

    override fun setViewId(): Int {
        return R.layout.opengles_activity
    }

    override fun initData() {
        super.initData()
        glsv.setShape(intent.getSerializableExtra("name") as Class<out Shape>)
    }

    override fun onPause() {
        super.onPause()
        glsv.onPause()
    }

    override fun onResume() {
        super.onResume()
        glsv.onResume()
    }

}