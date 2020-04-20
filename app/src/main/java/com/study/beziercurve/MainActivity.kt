package com.study.beziercurve

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        code1_bezier.setOnClickListener(this)
        code2_path_measure.setOnClickListener(this)
        code3_anim.setOnClickListener(this)
        code4_xfermode.setOnClickListener(this)
        code5_scroller_velocityTracker.setOnClickListener(this)
        code6_draw_flow.setOnClickListener(this)
        code7_svg.setOnClickListener(this)
        code8_canvas_clip.setOnClickListener(this)
        code8_canvas_draw.setOnClickListener(this)
        code8_canvas_text.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.code1_bezier -> startActivity(
                Intent(
                    this,
                    com.study.bezier.ui.ClientActivity::class.java
                )
            )
            R.id.code2_path_measure -> startActivity(
                Intent(
                    this,
                    com.study.pathmeasure.ui.PathMeasureClientActivity::class.java
                )
            )
            R.id.code3_anim -> startActivity(
                Intent(
                    this,
                    com.study.animation.ui.AnimationClientActivity::class.java
                )
            )
            R.id.code4_xfermode -> startActivity(
                Intent(
//                    this,
//                    com.zinc.xfermode.ClientActivity::class.java
                )
            )
            R.id.code5_scroller_velocityTracker -> startActivity(
                Intent(
//                    this,
//                    com.zinc.velocitytracker_scroller.BarActivity::class.java
                )
            )
            R.id.code6_draw_flow -> startActivity(
                Intent(
//                    this,
//                    com.zinc.flowlayout.ClientActivity::class.java
                )
            )
            R.id.code7_svg -> startActivity(
                Intent(
//                    this,
//                    com.zinc.svg.ClientActivity::class.java
                )
            )
            R.id.code8_canvas_clip -> startActivity(
                Intent(
//                    this,
//                    com.zinc.code8_canvas_clip.activity.ClientActivity::class.java
                )
            )
            R.id.code8_canvas_draw -> startActivity(
                Intent(
//                    this,
//                    com.zinc.code8_canvas_draw.activity.ClientActivity::class.java
                )
            )
            R.id.code8_canvas_text -> startActivity(
                Intent(
//                    this,
//                    com.zinc.code8_canvas_text_paint.ClientActivity::class.java
                )
            )
        }
    }
}
