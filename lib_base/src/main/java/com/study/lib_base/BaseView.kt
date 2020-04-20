package com.study.lib_base

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * @author dengdai
 * @date 2020/3/30
 * @description 基础View
 */
@Suppress("LeakingThis")
abstract class BaseView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 坐标画笔
    private var mCoordinatePaint: Paint? = null

    // 网格画笔
    private var mGridPaint: Paint? = null

    // 写字画笔
    private var mTextPaint: Paint? = null

    // 坐标颜色
    private var mCoordinateColor = 0
    private var mGridColor = 0

    // 网格宽度 50px
    private val mGridWidth = 50

    // 坐标线宽度
    private val mCoordinateLineWidth = 2.5f

    // 网格宽度
    private val mGridLineWidth = 1f

    // 字体大小
    private var mTextSize = 0f

    // 标柱的高度
    private val mCoordinateFlagHeight = 8f
    @JvmField
    protected var mWidth = 0f
    @JvmField
    protected var mHeight = 0f
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = measuredWidth.toFloat()
        mHeight = measuredHeight.toFloat()
    }

    private fun initCoordinate() {
        mCoordinateColor = Color.BLACK
        mGridColor = Color.LTGRAY
        mTextSize = 10f.spToPx().toFloat()
        mCoordinatePaint = Paint()
        mCoordinatePaint!!.isAntiAlias = true
        mCoordinatePaint!!.color = mCoordinateColor
        mCoordinatePaint!!.strokeWidth = mCoordinateLineWidth
        mGridPaint = Paint()
        mGridPaint!!.isAntiAlias = true
        mGridPaint!!.color = mGridColor
        mGridPaint!!.strokeWidth = mGridLineWidth
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.color = mCoordinateColor
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mTextPaint!!.textSize = mTextSize
    }

    protected abstract fun init(context: Context?)

    /**
     * 画坐标和网格，以画布中心点为原点
     * 坐标系
     * @param canvas 画布
     */
    protected fun drawCoordinate(canvas: Canvas) {
        val halfWidth = mWidth / 2
        val halfHeight = mHeight / 2

        // 画网格
        canvas.save()
        canvas.translate(halfWidth, halfHeight)
        var curWidth = mGridWidth
        // 画横线
        while (curWidth < halfWidth + mGridWidth) {

            // 向右画
            canvas.drawLine(
                curWidth.toFloat(),
                -halfHeight,
                curWidth.toFloat(),
                halfHeight,
                mGridPaint!!
            )
            // 向左画
            canvas.drawLine(
                -curWidth.toFloat(),
                -halfHeight,
                -curWidth.toFloat(),
                halfHeight,
                mGridPaint!!
            )

            // 画标柱
            canvas.drawLine(
                curWidth.toFloat(),
                0f,
                curWidth.toFloat(),
                -mCoordinateFlagHeight,
                mCoordinatePaint!!
            )
            canvas.drawLine(
                -curWidth.toFloat(),
                0f,
                -curWidth.toFloat(),
                -mCoordinateFlagHeight,
                mCoordinatePaint!!
            )

            // 标柱宽度（每两个画一个）
            if (curWidth % (mGridWidth * 2) == 0) {
                canvas.drawText(
                    curWidth.toString() + "",
                    curWidth.toFloat(),
                    mTextSize * 1.5f,
                    mTextPaint!!
                )
                canvas.drawText(
                    (-curWidth).toString(),
                    -curWidth.toFloat(),
                    mTextSize * 1.5f,
                    mTextPaint!!
                )
            }
            curWidth += mGridWidth
        }
        var curHeight = mGridWidth
        // 画竖线
        while (curHeight < halfHeight + mGridWidth) {

            // 向右画
            canvas.drawLine(
                -halfWidth,
                curHeight.toFloat(),
                halfWidth,
                curHeight.toFloat(),
                mGridPaint!!
            )
            // 向左画
            canvas.drawLine(
                -halfWidth,
                -curHeight.toFloat(),
                halfWidth,
                -curHeight.toFloat(),
                mGridPaint!!
            )

            // 画标柱
            canvas.drawLine(
                0f,
                curHeight.toFloat(),
                mCoordinateFlagHeight,
                curHeight.toFloat(),
                mCoordinatePaint!!
            )
            canvas.drawLine(
                0f,
                -curHeight.toFloat(),
                mCoordinateFlagHeight,
                -curHeight.toFloat(),
                mCoordinatePaint!!
            )

            // 标柱宽度（每两个画一个）
            if (curHeight % (mGridWidth * 2) == 0) {
                canvas.drawText(
                    curHeight.toString() + "",
                    -mTextSize * 2,
                    curHeight + mTextSize / 2,
                    mTextPaint!!
                )
                canvas.drawText(
                    (-curHeight).toString(),
                    -mTextSize * 2,
                    -curHeight + mTextSize / 2,
                    mTextPaint!!
                )
            }
            curHeight += mGridWidth
        }
        canvas.restore()

        // 画 x，y 轴
        canvas.drawLine(halfWidth, 0f, halfWidth, mHeight, mCoordinatePaint!!)
        canvas.drawLine(0f, halfHeight, mWidth, halfHeight, mCoordinatePaint!!)
    }

    /**
     * 转换 sp 至 px
     *
     * @param this@spToPx sp值
     * @return px值
     */
    private fun Float.spToPx(): Int {
        val fontScale =
            Resources.getSystem().displayMetrics.scaledDensity
        return (this * fontScale + 0.5f).toInt()
    }

    /**
     * 转换 dp 至 px
     *
     * @param this@dpToPx dp值
     * @return px值
     */
//    protected fun Float.dpToPx(): Int {
//        val metrics = Resources.getSystem().displayMetrics
//        return (this * metrics.density + 0.5f).toInt()
//    }
    protected fun Int.dpToPx(): Float {
        val metrics = Resources.getSystem().displayMetrics
        return (this * metrics.density + 0.5f)
    }
    init {
        initCoordinate()
        init(context)
    }
}