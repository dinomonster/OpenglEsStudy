package com.dino.openglesstudy

import android.view.View
import com.dino.openglesstudy.base.BaseActivity
import com.dino.openglesstudy.shape.ShapeListActivity
import com.dino.openglesstudy.utils.ViewUtils
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : BaseActivity(),View.OnClickListener {
    override fun setViewId(): Int {return R.layout.main_activity}

    override fun initData() {
        super.initData()
        ViewUtils.setOnClickListeners(this,audio_btn,opengles_btn)
    }

    override fun onClick(v: View?) {
        when(v){
//            audio_btn->{
//                nextActivity(AudioDemoActivity::class.java)
//            }
            opengles_btn->{
                nextActivity(ShapeListActivity::class.java)
            }
        }
    }
}
