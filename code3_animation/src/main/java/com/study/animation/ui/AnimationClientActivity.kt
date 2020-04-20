package com.study.animation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.study.animation.R
import kotlinx.android.synthetic.main.activity_animation_client.*

class AnimationClientActivity : AppCompatActivity() ,View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_client)
        radar.setOnClickListener(this)
        dial.setOnClickListener(this)
        interpolator.setOnClickListener(this)
        shoppingCart.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.radar->startActivity(Intent(this,RadarActivity::class.java))
        }
    }
}
