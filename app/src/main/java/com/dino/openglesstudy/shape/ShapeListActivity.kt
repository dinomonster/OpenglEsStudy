package com.dino.openglesstudy.shape

import android.content.Intent
import android.view.View
import com.dino.openglesstudy.R
import com.dino.openglesstudy.base.BaseActivity
import com.dino.openglesstudy.utils.ViewUtils
import kotlinx.android.synthetic.main.triangle_activity.*

class ShapeListActivity : BaseActivity(), View.OnClickListener {
    override fun setViewId(): Int {
        return R.layout.triangle_activity
    }

    override fun initData() {
        super.initData()
        ViewUtils.setOnClickListeners(this,
                triangle_btn, triangle_camera_btn, colorful_triangle_camera_btn,
                move_colorful_triangle_camera_btn,square_btn,oval_btn,cube_btn,
                cone_btn,cylinder_btn,ball_btn,ballwithlight_btn)
    }

    override fun onClick(v: View?) {
        when (v) {
            triangle_btn -> {
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",Triangle::class.java) })
            }
            triangle_camera_btn -> {
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",TriangleCamera::class.java) })
            }
            colorful_triangle_camera_btn -> {
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",ColorfulTriangleCamera::class.java) })
            }
            move_colorful_triangle_camera_btn -> {
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",MoveColorfulTriangleCamera::class.java) })
            }
            square_btn -> {
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",Square::class.java) })
            }
            oval_btn -> {
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",Oval::class.java) })
            }
            cube_btn -> {
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",Cube::class.java) })
            }
            cone_btn -> {
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",Cone::class.java) })
            }
            cylinder_btn -> {
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",Cylinder::class.java) })
            }
            ball_btn->{
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",Ball::class.java) })
            }
            ballwithlight_btn->{
                startActivity(Intent(TriangleActivity@this,ShapeActivity::class.java)
                        .apply { putExtra("name",Cylinder::class.java) })
            }
        }
    }
}
