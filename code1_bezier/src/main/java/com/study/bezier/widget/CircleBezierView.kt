package com.study.bezier.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.study.lib_base.BaseView
import java.util.*

/**
 * @author dengdai
 * @date 2020/3/30
 * @description 用贝塞尔曲线绘制圆的过程
 */
class CircleBezierView : BaseView {
    // 圆的中心点
    private var mCenterPoint: PointF? = null

    // 圆半径
    private var mRadius = 0f

    // 控制点列表，顺序为：右上、右下、左下、左上
    private var mControlPointList: MutableList<PointF>? = null

    // 控制点占半径的比例
    private var mRatio = 0.55f

    // 圆的路径
    private var mPath: Path? = null

    // 绘制贝塞尔曲线的画笔
    private var mPaint: Paint? = null

    // 绘制圆的画笔
    private var mCirclePaint: Paint? = null

    // 绘制控制线的画笔
    private var mLinePaint: Paint? = null

    // 控制线的颜色
    private val mLineColor: IntArray by lazy {
        intArrayOf(
            Color.parseColor("#f4ea2a"),//黄色
            Color.parseColor("#1afa29"),//绿色
            Color.parseColor("#efb336"),//橙色
            Color.parseColor("#e89abe") //粉色
        )
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * 设置比例
     *
     * @param ratio 比例，0-1
     */
    fun setRatio(ratio: Float) {
        mRatio = ratio
        calculateControlPoint()
        invalidate()
    }

    override fun init(context: Context?) {
        val width = context!!.resources.displayMetrics.widthPixels
        mRadius = (width / 3).toFloat()
        mCenterPoint = PointF(0F, 0F)
        mControlPointList = ArrayList()
        mPath = Path()
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.color = Color.parseColor("#1296db")
        mCirclePaint = Paint()
        mCirclePaint!!.isAntiAlias = true
        mCirclePaint!!.style = Paint.Style.STROKE
        mCirclePaint!!.strokeWidth = 2f//线宽
        mCirclePaint!!.color = Color.RED
        mLinePaint = Paint()
        mLinePaint!!.isAntiAlias = true
        mLinePaint!!.style = Paint.Style.STROKE
        mLinePaint!!.strokeWidth = 2.dpToPx()
    }

    override fun onDraw(canvas: Canvas) {
        drawCoordinate(canvas)
        canvas.translate(mWidth / 2, mHeight / 2)
        mPath!!.reset()
        (0..3).forEach { i ->//贝塞斯曲线计算
            if (i == 0) {
                mPath!!.moveTo(mControlPointList!![i * 3].x, mControlPointList!![i * 3].y)
            } else {
                mPath!!.lineTo(mControlPointList!![i * 3].x, mControlPointList!![i * 3].y)
            }
            val endPointIndex: Int = if (i == 3) {
                0
            } else {
                i * 3 + 3
            }
            mPath!!.cubicTo(
                mControlPointList!![i * 3 + 1].x, mControlPointList!![i * 3 + 1].y,
                mControlPointList!![i * 3 + 2].x, mControlPointList!![i * 3 + 2].y,
                mControlPointList!![endPointIndex].x, mControlPointList!![endPointIndex].y
            )
        }

        // 绘制贝塞尔曲线
        canvas.drawPath(mPath!!, mPaint!!)

        // 绘制圆
        canvas.drawCircle(mCenterPoint!!.x, mCenterPoint!!.y, mRadius, mCirclePaint!!)

        // 绘制控制线
        mControlPointList!!.indices.forEach { i ->
            // 设置颜色
            mLinePaint!!.color = mLineColor[i / 3]
            val endPointIndex = if (i == mControlPointList!!.size - 1) 0 else i + 1
            canvas.drawLine(
                mControlPointList!![i].x,
                mControlPointList!![i].y,
                mControlPointList!![endPointIndex].x,
                mControlPointList!![endPointIndex].y,
                mLinePaint!!
            )
        }
    }

    /**
     * 计算圆的控制点
     */
    private fun calculateControlPoint() {
        // 计算 中间控制点到端点的距离
        val controlWidth = mRatio * mRadius
        mControlPointList!!.clear()

        // 右上
        mControlPointList!!.add(PointF(0F, -mRadius))
        mControlPointList!!.add(PointF(controlWidth, -mRadius))
        mControlPointList!!.add(PointF(mRadius, -controlWidth))

        // 右下
        mControlPointList!!.add(PointF(mRadius, 0F))
        mControlPointList!!.add(PointF(mRadius, controlWidth))
        mControlPointList!!.add(PointF(controlWidth, mRadius))

        // 左下
        mControlPointList!!.add(PointF(0F, mRadius))
        mControlPointList!!.add(PointF(-controlWidth, mRadius))
        mControlPointList!!.add(PointF(-mRadius, controlWidth))
        // 左上
        mControlPointList!!.add(PointF(-mRadius, 0F))
        mControlPointList!!.add(PointF(-mRadius, -controlWidth))
        mControlPointList!!.add(PointF(-controlWidth, -mRadius))
    }
}