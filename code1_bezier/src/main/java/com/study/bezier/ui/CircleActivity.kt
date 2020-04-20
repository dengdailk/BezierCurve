package com.study.bezier.ui

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.study.bezier.R
import kotlinx.android.synthetic.main.activity_circle.*

class CircleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle)
        ratio_seek_bar.max = 100
        circle_bezier_view.setRatio(0.55F)
        tv_ratio.text=String.format(getString(R.string.ratio),"0.55")
        ratio_seek_bar.progress = 55

        ratio_seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                val r = progress /100.0
                tv_ratio.text = String.format(getString(R.string.ratio), "" + r)
                circle_bezier_view.setRatio(r.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

    }
}
