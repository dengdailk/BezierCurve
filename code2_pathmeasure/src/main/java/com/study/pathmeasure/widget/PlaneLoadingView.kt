package com.study.pathmeasure.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import com.study.lib_base.BaseView
import com.study.pathmeasure.R
import kotlin.math.atan2

/**
 * @author dengdai
 * @date 2020/3/31.
 * @description
 */
class PlaneLoadingView : BaseView {
    // PathMeasure 测量过程中的坐标
    private lateinit var mPos: FloatArray

    // PathMeasure 测量过程中的正切
    private lateinit var mTan: FloatArray

    // 圈的画笔
    private lateinit var mCirclePaint: Paint

    // 箭头图片
    private var mArrowBitmap: Bitmap? = null

    // 圆路径
    private var mCirclePath: Path? = null

    // 路径测量
    private var mPathMeasure: PathMeasure? = null

    // 当前移动值
    private var mCurrentValue = 0f
    private var mMatrix: Matrix? = null
    private lateinit var valueAnimator: ValueAnimator

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun init(context: Context?) {
        // 初始化 画笔 [抗锯齿、不填充、红色、线条2px]
        mCirclePaint = Paint()
        mCirclePaint.isAntiAlias = true
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.color = Color.RED
        mCirclePaint.strokeWidth = 2f

        // 获取图片
        mArrowBitmap = BitmapFactory.decodeResource(context!!.resources, R.drawable.arrow, null)

        // 初始化 圆路径 [圆心(0,0)、半径200px、顺时针画]
        mCirclePath = Path()
        mCirclePath!!.addCircle(0f, 0f, 200f, Path.Direction.CW)

        // 初始化 装载 坐标 和 正余弦 的数组
        mPos = FloatArray(2)
        mTan = FloatArray(2)

        // 初始化 PathMeasure 并且关联 圆路径
        mPathMeasure = PathMeasure()
        mPathMeasure!!.setPath(mCirclePath, false)

        // 初始化矩阵
        mMatrix = Matrix()

        // 初始化 估值器 [区间0-1、时长5秒、线性增长、无限次循环]
        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 5000
        // 匀速增长
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener { // 第一种做法：通过自己控制，是箭头在原来的位置继续运行
            mCurrentValue += DELAY
            if (mCurrentValue >= 1) {
                mCurrentValue -= 1f
            }

            // 第二种做法：直接获取可以通过估值器，改变其变动规律
    //                mCurrentValue = (float) animation.getAnimatedValue();
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        // 画网格和坐标轴
        drawCoordinate(canvas)

        // 移至canvas中间
        canvas.translate(mWidth / 2, mHeight / 2)

        // 画圆路径
        canvas.drawPath(mCirclePath!!, mCirclePaint)

        // 测量 pos(坐标) 和 tan(正切)
        mPathMeasure!!.getPosTan(mPathMeasure!!.length * mCurrentValue, mPos, mTan)

        // 计算角度
        val degree = (atan2(
            mTan[1].toDouble(),
            mTan[0].toDouble()
        ) * 180 / Math.PI).toFloat()
        Log.i(
            "PlaneLoadingView_1",
            "------------pos[0] = " + mPos[0] + "; pos[1] = " + mPos[1]
        )
        Log.i(
            "PlaneLoadingView_2",
            "------------tan[0](cos) = " + mTan[0] + "; tan[1](sin) = " + mTan[1]
        )
        Log.i(
            "PlaneLoadingView_3",
            "path length = " + mPathMeasure!!.length * mCurrentValue
        )
        Log.i("PlaneLoadingView_4", "degree = $degree")

        // 重置矩阵
        mMatrix!!.reset()
        // 设置旋转角度
        mMatrix!!.postRotate(
            degree,
            mArrowBitmap!!.width / 2.toFloat(),
            mArrowBitmap!!.height / 2.toFloat()
        )
        // 设置偏移量
        mMatrix!!.postTranslate(
            mPos[0] - mArrowBitmap!!.width / 2,
            mPos[1] - mArrowBitmap!!.height / 2
        )

        // 画原点
        canvas.drawCircle(0f, 0f, 3f, mCirclePaint)

        // 画箭头，使用矩阵旋转
        canvas.drawBitmap(mArrowBitmap!!, mMatrix!!, mCirclePaint)

        // 画在 箭头 图标的中心点
        canvas.drawCircle(mPos[0], mPos[1], 3f, mCirclePaint)
    }

    fun startLoading() {
        valueAnimator.start()
    }

    fun stopLoading() {
        valueAnimator.cancel()
    }

    companion object {
        private const val DELAY = 0.005f
    }
}