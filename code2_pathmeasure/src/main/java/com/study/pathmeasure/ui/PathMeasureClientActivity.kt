package com.study.pathmeasure.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.study.pathmeasure.R
import kotlinx.android.synthetic.main.activity_path_client.*

class PathMeasureClientActivity : AppCompatActivity(), OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_client)

        bt_arrow.setOnClickListener(this)
        loading.setOnClickListener(this)
        boat.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.bt_arrow->startActivity(Intent(this,PlaneLoadingActivity::class.java))
            R.id.loading->startActivity(Intent(this,CommonActivity::class.java))
            R.id.boat->startActivity(Intent(this,BoatLoadingActivity::class.java))
        }
    }
}
