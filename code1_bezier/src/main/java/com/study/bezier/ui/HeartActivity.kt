package com.study.bezier.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.study.bezier.R
import kotlinx.android.synthetic.main.activity_heart.*
import kotlinx.android.synthetic.main.activity_heart.view.*

class HeartActivity : AppCompatActivity(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart)
        start.setOnClickListener(this)
        reset.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.start-> heart_view.start()
            R.id.reset->heart_view.reset()
        }
    }
}
