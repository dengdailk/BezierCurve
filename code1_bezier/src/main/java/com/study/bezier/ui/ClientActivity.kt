package com.study.bezier.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.study.bezier.R
import kotlinx.android.synthetic.main.activity_client.*

class ClientActivity : AppCompatActivity() , OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        drawCircle.setOnClickListener(this)
        showBezierPlay.setOnClickListener(this)
        diy.setOnClickListener(this)
        changeToHeart.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.drawCircle->startActivity(Intent(this,CircleActivity::class.java))
            R.id.showBezierPlay->startActivity(Intent(this,BezierActivity::class.java))
            R.id.diy->startActivity(Intent(this,DIYBezierActivity::class.java))
            R.id.changeToHeart->startActivity(Intent(this,HeartActivity::class.java))
        }
    }
}
