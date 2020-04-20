package com.study.bezier.ui

import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.study.bezier.R
import com.study.bezier.widget.DIYBezierView
import kotlinx.android.synthetic.main.activity_d_i_y_bezier.*

/**
 * 带动画过程的贝塞尔
 */
class DIYBezierActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_d_i_y_bezier)
        diy_bezier_view.setIsShowHelpLine(true)
        show_line_switch.isChecked = true
        rg_status.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.status_free -> diy_bezier_view.setStatus(DIYBezierView.Status.FREE)
                R.id.status_mirror_diff -> diy_bezier_view.setStatus(DIYBezierView.Status.MIRROR_DIFF)
                R.id.status_three -> diy_bezier_view.setStatus(DIYBezierView.Status.THREE)
                R.id.status_mirror_same -> diy_bezier_view.setStatus(DIYBezierView.Status.MIRROR_SAME)
            }
        }
        show_line_switch.setOnCheckedChangeListener { _, isChecked ->
            diy_bezier_view.setIsShowHelpLine(isChecked)
        }
        reset.setOnClickListener(this)
        log.setOnClickListener(this)
    }

    private fun px2dip(context: Context, pxValue: Float): Int {
        val density = context.resources.displayMetrics.density
        return (pxValue / density + 0.5f).toInt()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.reset -> diy_bezier_view.reset()
            R.id.log -> {
                val controlPointList: List<PointF> = diy_bezier_view.controlPointList!!
                val stringBuilder = StringBuilder()
                stringBuilder.append("\n")
                controlPointList.indices.forEach { i ->
                    stringBuilder.append("第")
                        .append(i)
                        .append("个点坐标(单位dp)：[")
                        .append(px2dip(this, controlPointList[i].x))
                        .append(", ")
                        .append(px2dip(this, controlPointList[i].y))
                        .append("]")
                        .append("\n")
                }
                Log.i("DIY Bezier", "控制点日志：$stringBuilder")

            }
        }
    }
}
