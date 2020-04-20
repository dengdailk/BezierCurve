package com.study.pathmeasure.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.study.pathmeasure.R
import kotlinx.android.synthetic.main.activity_boat_loading.*

class BoatLoadingActivity : AppCompatActivity(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boat_loading)
        start.setOnClickListener(this)
        stop.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.start->boat_wave_view.startAnim()
            R.id.stop->boat_wave_view.stopAnim()
        }
    }
}
