package com.study.pathmeasure.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.study.pathmeasure.R
import kotlinx.android.synthetic.main.activity_plane_loading.*

class PlaneLoadingActivity : AppCompatActivity() , OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plane_loading)
        start.setOnClickListener(this)
        stop.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.start->plane_loading_view.startLoading()
            R.id.stop->plane_loading_view.stopLoading()
        }
    }
}
