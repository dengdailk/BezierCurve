package com.study.bezier.widget

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.study.bezier.R
import com.study.bezier.ui.BezierActivity
import kotlinx.android.synthetic.main.dialog_bezier_setting.*

/**
 * @author dengdai
 * @date 2020/3/31.
 * GitHub：
 * email：291996307@qq.com
 * description：
 */
class BezierDialog : AppCompatDialogFragment() {
    private var dismissAnim: Animation? = null
    private var isDismissing = false

    // 是否显示降阶线
    private var isShowReduceOrderLine = false

    // 是否循环播放
    private var isLoopPlay = false

    // 阶数
    private var order = 0

    // 速率
    private var rate = 0
    fun setShowReduceOrderLine(showReduceOrderLine: Boolean) {
        isShowReduceOrderLine = showReduceOrderLine
    }

    fun setLoopPlay(loopPlay: Boolean) {
        isLoopPlay = loopPlay
    }

    fun setOrder(order: Int) {
        var order = order
        when {
            order > ORDER_MAX -> {
                order = ORDER_MAX
            }
            order < 1 -> {
                order = 1
            }
        }
        this.order = order - 1
    }

    fun setRate(rate: Int) {
        var rate = rate/RATE_INTERVAL
        if (rate <= RATE_MAX) {
            if (rate < 1) {
                rate = 1
            }
        } else {
            rate = RATE_MAX
        }
        this.rate = rate - 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TranslucentNoTitle)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_bezier_setting, null)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        reduce_switch.isChecked = isShowReduceOrderLine
        loop_switch.isChecked = isLoopPlay
        order_seekbar.progress = order
        tv_order.text = getString(R.string.order, order + 1)
        rate_seekbar.progress = rate
        tv_rate.text = getString(R.string.rate, (rate + 1) * RATE_INTERVAL)

        // 设置阶数最大值
        order_seekbar.max = ORDER_MAX - 1

        // 设置速率最大值
        rate_seekbar.max = RATE_MAX - 1
        this.dialog!!.window
            ?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        ll_menu.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_show_anim))
        dismissAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_dismiss_anim)
        dismissAnim?.run {
            setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    super@BezierDialog.dismiss()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            dismiss()
            keyCode == KeyEvent.KEYCODE_BACK
        }
        order_seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (activity == null || activity !is BezierActivity) {
                    return
                }
                tv_order.text = getString(R.string.order, progress + 1)
                val bezierActivity = activity as BezierActivity?
                bezierActivity!!.setOrder(progress + 1)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        rate_seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (activity == null || activity !is BezierActivity) {
                    return
                }
                val result = (progress + 1) * RATE_INTERVAL
                tv_rate.text = getString(R.string.rate, result)
                val bezierActivity = activity as BezierActivity?
                bezierActivity!!.setRate(result)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        loop_switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (activity == null || activity !is BezierActivity) {
                return@OnCheckedChangeListener
            }
            val bezierActivity = activity as BezierActivity?
            bezierActivity!!.setLoopPlay(isChecked)
        })
        reduce_switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (activity == null || activity !is BezierActivity) {
                return@OnCheckedChangeListener
            }
            val bezierActivity = activity as BezierActivity?
            bezierActivity!!.setShowReduceOrderLine(isChecked)
        })
        rl_background.setOnClickListener{ dismiss() }
        isDismissing = false
    }

    override fun dismiss() {
        if (isDismissing) {
            return
        }
        isDismissing = true
        ll_menu!!.startAnimation(dismissAnim)
    }

    companion object {
        // 阶级最大值
        private const val ORDER_MAX = 7

        // 速率最大值
        private const val RATE_MAX = 5

        // 速率的间隔
        private const val RATE_INTERVAL = 5
        val instance: BezierDialog
            get() {
                val bundle = Bundle()
                val fragment = BezierDialog()
                fragment.arguments = bundle
                return fragment
            }
    }
}