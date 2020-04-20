package com.study.bezier.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.study.lib_base.BaseView
import kotlin.math.pow
import kotlin.math.sin

/**
 * @author dengdai
 * @date 2020/3/30
 * @description 心
 */
class HeartView : BaseView {
    private var mPath: Path? = null
    private var mPaint: Paint? = null
    private var mHeartPointList: ArrayList<PointF>? = null
    private var mCirclePointList: ArrayList<PointF>? = null
    private var mCurPointList: ArrayList<PointF>? = null
    private lateinit var mAnimator: ValueAnimator

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun init(context: Context?) {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.color = Color.RED
        mPaint!!.style = Paint.Style.FILL
        mPath = Path()
        mHeartPointList = ArrayList()
        mHeartPointList!!.add(PointF(0F, (-38).dpToPx()))
        mHeartPointList!!.add(PointF(50.dpToPx(), (-103).dpToPx()))
        mHeartPointList!!.add(PointF(112.dpToPx(), (-61).dpToPx()))
        mHeartPointList!!.add(PointF(112.dpToPx(), (-12).dpToPx()))
        mHeartPointList!!.add(PointF(112.dpToPx(), 37.dpToPx()))
        mHeartPointList!!.add(PointF(51.dpToPx(), 90.dpToPx()))
        mHeartPointList!!.add(PointF(0F, 129.dpToPx()))
        mHeartPointList!!.add(PointF((-51).dpToPx(), 90.dpToPx()))
        mHeartPointList!!.add(PointF((-112).dpToPx(), 37.dpToPx()))
        mHeartPointList!!.add(PointF((-112).dpToPx(), (-12).dpToPx()))
        mHeartPointList!!.add(PointF((-112).dpToPx(), (-61).dpToPx()))
        mHeartPointList!!.add(PointF((-50).dpToPx(), (-103).dpToPx()))
        mCirclePointList = ArrayList()
        mCirclePointList!!.add(PointF(0F, (-89).dpToPx()))
        mCirclePointList!!.add(PointF(50.dpToPx(), (-89).dpToPx()))
        mCirclePointList!!.add(PointF(90.dpToPx(), (-49).dpToPx()))
        mCirclePointList!!.add(PointF(90.dpToPx(), 0F))
        mCirclePointList!!.add(PointF(90.dpToPx(), 50.dpToPx()))
        mCirclePointList!!.add(PointF(50.dpToPx(), 90.dpToPx()))
        mCirclePointList!!.add(PointF(0F, 90.dpToPx()))
        mCirclePointList!!.add(PointF((-49).dpToPx(), 90.dpToPx()))
        mCirclePointList!!.add(PointF((-89).dpToPx(), 50.dpToPx()))
        mCirclePointList!!.add(PointF((-89).dpToPx(), 0F))
        mCirclePointList!!.add(PointF((-89).dpToPx(), (-49).dpToPx()))
        mCirclePointList!!.add(PointF((-49).dpToPx(), (-89).dpToPx()))
        mCurPointList = ArrayList()
        mCurPointList!!.add(PointF(0F, (-89).dpToPx()))
        mCurPointList!!.add(PointF(50.dpToPx(), (-89).dpToPx()))
        mCurPointList!!.add(PointF(90.dpToPx(), (-49).dpToPx()))
        mCurPointList!!.add(PointF(90.dpToPx(), 0F))
        mCurPointList!!.add(PointF(90.dpToPx(), 50.dpToPx()))
        mCurPointList!!.add(PointF(50.dpToPx(), 90.dpToPx()))
        mCurPointList!!.add(PointF(0F, 90.dpToPx()))
        mCurPointList!!.add(PointF((-49).dpToPx(), 90.dpToPx()))
        mCurPointList!!.add(PointF((-89).dpToPx(), 50.dpToPx()))
        mCurPointList!!.add(PointF((-89).dpToPx(), 0F))
        mCurPointList!!.add(PointF((-89).dpToPx(), (-49).dpToPx()))
        mCurPointList!!.add(PointF((-49).dpToPx(), (-89).dpToPx()))
        mAnimator = ValueAnimator.ofFloat(0f, 1f)
        mAnimator.duration = 1500
        mAnimator.addUpdateListener { animation ->
            val x = animation.animatedValue as Float
            val factor = 0.15f
            val value =
                2.0.pow(-10 * x.toDouble()) * sin((x - factor / 4) * (2 * Math.PI) / factor) + 1
            for (i in mCurPointList!!.indices) {
                val startPoint = mCirclePointList!![i]
                val endPoint = mHeartPointList!![i]
                mCurPointList!![i].x =
                    startPoint.x + ((endPoint.x - startPoint.x) * value).toFloat()
                mCurPointList!![i].y =
                    startPoint.y + ((endPoint.y - startPoint.y) * value).toFloat()
            }
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawCoordinate(canvas)
        canvas.translate(mWidth / 2, mHeight / 2)
        mPath!!.reset()
        for (i in 0..3) {
            if (i == 0) {
                mPath!!.moveTo(mCurPointList!![i * 3].x, mCurPointList!![i * 3].y)
            } else {
                mPath!!.lineTo(mCurPointList!![i * 3].x, mCurPointList!![i * 3].y)
            }
            val endPointIndex: Int = if (i == 3) {
                0
            } else {
                i * 3 + 3
            }
            mPath!!.cubicTo(
                mCurPointList!![i * 3 + 1].x, mCurPointList!![i * 3 + 1].y,
                mCurPointList!![i * 3 + 2].x, mCurPointList!![i * 3 + 2].y,
                mCurPointList!![endPointIndex].x, mCurPointList!![endPointIndex].y
            )
        }
        canvas.drawPath(mPath!!, mPaint!!)
    }

    fun start() {
        if (mAnimator.isRunning) {
            return
        }
        mAnimator.start()
    }

    fun reset() {
        mAnimator.reverse()
    }
}