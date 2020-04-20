package com.study.pathmeasure.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import com.study.lib_base.BaseView
import kotlin.math.abs

/**
 * @author dengdai
 * @date 2020/4/3.
 * GitHub：
 * email：291996307@qq.com
 * description：粘性加载圈
 */
class LoadingView : BaseView {
    private var mPath: Path? = null
    private var mPaint: Paint? = null
    private var mPathMeasure: PathMeasure? = null
    private var mAnimatorValue = 0f
    private var mDst: Path? = null
    private var mLength = 0f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun init(context: Context?) {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = 20f
        mPaint!!.strokeCap = Paint.Cap.ROUND
        mPath = Path()
        mPath!!.addCircle(0f, 0f, 200f, Path.Direction.CCW)
        mPathMeasure = PathMeasure()
        mPathMeasure!!.setPath(mPath, true)
        mLength = mPathMeasure!!.length
        mDst = Path()
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener {
            mAnimatorValue = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.duration = 2000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        drawCoordinate(canvas)
        canvas.translate(mWidth / 2, mHeight / 2)

        // 需要重置，否则受上次影响，因为getSegment方法是添加而非替换
        mDst!!.reset()
        // 4.4版本以及之前的版本，需要使用这行代码，否则getSegment无效果
        // 导致这个原因是 硬件加速问题导致
        mDst!!.lineTo(0f, 0f)
        val stop = mLength * mAnimatorValue
        val start =
            (stop - (0.5 - abs(mAnimatorValue - 0.5)) * mLength).toFloat()
        mPathMeasure!!.getSegment(start, stop, mDst, true)
        canvas.drawPath(mDst!!, mPaint!!)
    }
}