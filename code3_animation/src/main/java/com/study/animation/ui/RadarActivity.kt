package com.study.animation.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.study.animation.R
import com.study.animation.widget.RadarChartView
import kotlinx.android.synthetic.main.activity_radar.*
import java.util.*
import kotlin.collections.ArrayList

class RadarActivity : AppCompatActivity() ,View.OnClickListener{
    // 数据最大数
    private val DATA_MAX = 4

    // 平均数最大数
    private val BASE_MAX = 3

    // 维度最大数
    private val DIMEN_MAX = 9

    // 基线数据
    private var baseDataCount = 1

    // 比较数据
    private var dataCount = 2

    // 维度
    private var dimenCount = 6

    private val textDataList: MutableList<String> = ArrayList()
    private var isShowText = false

    private val BASE_COLOR: IntArray? by lazy {
        intArrayOf(
            Color.parseColor("#00BFFF"),//蓝色
            Color.parseColor("#FFA500"),//橙色
            Color.parseColor("#FFB6C1")//粉色
        )
    }

    private val DATA_COLOR: IntArray? by lazy {
        intArrayOf(
            Color.parseColor("#DC143C"),//红色
            Color.parseColor("#FFD700"),//黄色
            Color.parseColor("#00FF7F"),//绿色
            Color.parseColor("#9932CC")//紫色
        )
    }
    private val baseDataList: MutableList<RadarChartView.Data> = ArrayList()
    private val dataList: MutableList<RadarChartView.Data> = ArrayList()
    private val random:Random = Random()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radar)

        data_seek_bar.max = DATA_MAX
        base_seek_bar.max = BASE_MAX
        dimen_seek_bar.max = DIMEN_MAX

        data_seek_bar.progress = dataCount
        base_seek_bar.progress = baseDataCount
        dimen_seek_bar.progress = dimenCount

        tv_data_num.text = String.format(getString(R.string.data_num),dataCount)
        tv_base_num.text = String.format(getString(R.string.base_num),dataCount)
        tv_dimen_num.text = String.format(getString(R.string.dimen_num),dataCount)

        data_seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                dataCount = progress
                tv_data_num.text = String.format(getString(R.string.data_num), progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        base_seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                baseDataCount = progress
                tv_base_num.text = String.format(getString(R.string.base_num), progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        dimen_seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                dimenCount = progress + 3
                tv_dimen_num.text = String.format(getString(R.string.dimen_num), dimenCount)
                radar_chart_view.setDimenCount(dimenCount)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        isShowText = true
        sw_text_des.isChecked = isShowText
        sw_text_des.setOnCheckedChangeListener { buttonView, isChecked ->
            isShowText = isChecked
        }

        run.setOnClickListener(this)
        stop.setOnClickListener(this)
        reset.setOnClickListener(this)
    }

    private fun createData(
        dataList: MutableList<RadarChartView.Data>,
        count: Int,
        dimen: Int,
        colorList: IntArray?
    ) {
        dataList.clear()
        for (i in 0 until count) {
            val list: ArrayList<Float> =
                ArrayList()
            for (j in 0 until dimen) {
                list.add(random.nextFloat())
            }
            val data: RadarChartView.Data = RadarChartView.Data(list, colorList!![i % colorList.size])
            dataList.add(data)
        }
    }
    private fun createTextData() {
        textDataList.clear()
        for (i in 0 until dimenCount) {
            textDataList.add("第" + i + "维")
        }
    }
    private fun onRun() {
        createData(dataList, dataCount, dimenCount, DATA_COLOR)
        createData(baseDataList, baseDataCount, dimenCount, BASE_COLOR)
        if (isShowText) {
            createTextData()
            radar_chart_view.setTextDataList(textDataList)
        } else {
            radar_chart_view.setTextDataList(ArrayList<String>())
        }
        radar_chart_view.setBaseDataList(baseDataList)
        radar_chart_view.setDataList(dataList)
        radar_chart_view.start()
    }

    override fun onClick(v: View?) {
       when(v?.id){
           R.id.run->onRun()
           R.id.stop->radar_chart_view.stop()
           R.id.reset->radar_chart_view.reset()
       }
    }
}
