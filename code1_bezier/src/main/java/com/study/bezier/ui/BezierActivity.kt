package com.study.bezier.ui

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.study.bezier.R
import com.study.bezier.widget.BezierDialog
import com.study.bezier.widget.BezierView
import kotlinx.android.synthetic.main.activity_bezier.*

class BezierActivity : AppCompatActivity(), OnClickListener {


    // 是否显示降阶线
    private var isShowReduceOrderLine: Boolean = true

    // 是否循环播放
    private var isLoopPlay = false

    // 阶数（默认五阶）
    private var order = 5

    // 速率（默认10个点的跳过播放）
    private var rate = 10
    private var dialog:BezierDialog = BezierDialog.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bezier)
        bezier_view.setIsShowReduceOrderLine(isShowReduceOrderLine)

        bezier_view.setOrder(order)
        bezier_view.setRate(rate)
        bezier_view.setIsLoop(isLoopPlay)
        iv_setting.setOnClickListener(this)
        iv_play.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_setting->{
                if (bezier_view.getState() == BezierView.RUNNING) {
                    Toast.makeText(this, "动画播放中，请稍等...", Toast.LENGTH_SHORT).show()
                    return
                }
                dialog.setShowReduceOrderLine(isShowReduceOrderLine)
                dialog.setLoopPlay(isLoopPlay)
                dialog.setOrder(order)
                dialog.setRate(rate)
                dialog.show(supportFragmentManager,"BezierActivity")
            }
            R.id.iv_play->{
                when {
                    bezier_view.getState() == BezierView.RUNNING -> {    // 运行中
                        iv_play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icons_play))
                        bezier_view.pause()
                    }
                    bezier_view.getState() == BezierView.STOP -> { // 已暂停
                        iv_play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icons_pause))
                        bezier_view.pause()
                    }
                    else -> {
                        iv_play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icons_pause))
                        bezier_view.start()
                    }
                }
            }
        }
    }

    fun setShowReduceOrderLine(showReduceOrderLine: Boolean) {
        this.isShowReduceOrderLine = showReduceOrderLine
        bezier_view.setIsShowReduceOrderLine(isShowReduceOrderLine)
    }
    fun setLoopPlay(loopPlay:Boolean){
        this.isLoopPlay = loopPlay
        bezier_view.setIsLoop(isLoopPlay)
    }
    fun setOrder(order: Int) {
        this.order = order
        bezier_view.setOrder(order)
        bezier_view.invalidate()
    }
    fun setRate(rate: Int) {
        this.rate = rate
        bezier_view.setRate(rate)
    }

    fun resetPlayBtn() {
        iv_play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icons_play))
    }
}
