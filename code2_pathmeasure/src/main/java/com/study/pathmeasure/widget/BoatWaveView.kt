package com.study.pathmeasure.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.study.lib_base.BaseView
import com.study.pathmeasure.R

/**
 * @author dengdai
 * @date 2020/4/3.
 * GitHub：
 * email：291996307@qq.com
 * description：
 */
class BoatWaveView : BaseView {

    // 浪花的宽度
    private var waveLength = 0

    // 小船浪花的高度
    private val boatWaveHeight = 20

    // 波浪高度
    private val waveHeight = 35

    // 浪花每次的偏移量
    private val waveOffset = 5

    private var isInit = false


    private lateinit var mWavePaint: Paint

    // 海浪的路径
    private lateinit var mWavePath: Path

    // 小船的浪路径
    private lateinit var mBoatWavePath: Path

    // 小船的路径
    private lateinit var mBoatPath: Path

    private lateinit var mBoatPathMeasure: PathMeasure

    // 小船的浪色值
    private var mBoatBlue = 0

    // 浪花的色值
    private var mWaveBlue = 0

    // 小船当前所处的值
    private var mCurValue = 0f

    // 浪花当前的偏移量
    private var mCurWaveOffset = 0

    // 小船的浪花偏移量
    private var mBoatWaveOffset = 0

    // 用于变换小船的
    private var mMatrix: Matrix? = null

    // 小船的图片
    private var mBoatBitmap: Bitmap? = null

    private var mAnimator: ValueAnimator? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun init(context: Context?) {
        mWavePaint = Paint()
        mWavePaint.isAntiAlias = true

        mBoatPath = Path()
        mWavePath = Path()
        mBoatWavePath = Path()

        mBoatPathMeasure = PathMeasure()

        mBoatBlue = ContextCompat.getColor(context!!, R.color.color_boat_blue)
        mWaveBlue = ContextCompat.getColor(context, R.color.color_wave_blue)

        mMatrix = Matrix()

        //加载图片
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = 1
        mBoatBitmap = BitmapFactory.decodeResource(resources, R.mipmap.boat, options)


        mAnimator = ValueAnimator.ofFloat(0F, 1F)
        mAnimator!!.duration = 4000
        mAnimator!!.repeatCount = ValueAnimator.INFINITE
        mAnimator!!.addUpdateListener {
            mCurValue = it.animatedValue as Float
            mCurWaveOffset = ((mCurWaveOffset + waveOffset) % mWidth).toInt()
            mBoatWaveOffset = ((mBoatWaveOffset + waveOffset / 2) % mWidth).toInt()
            postInvalidate()
        }
    }
    /**
     * @param path       路径
     * @param length     浪花的宽度
     * @param height     浪花的高度
     * @param isClose    是否要闭合
     * @param lengthTime 浪花长的倍数
     */
    private fun initPath(
        path: Path,
        length: Int,
        height: Int,
        isClose: Boolean,
        lengthTime: Float
    ) {
    //初始化小船路径
       path.moveTo((-length).toFloat(),mHeight/2)

           var i = -length
           while (i < mWidth * lengthTime + length) {
               // rQuadTo 和 quadTo 区别在于
               // rQuadTo 是相对上一个点 而 quadTo是相对于画布
               path.rQuadTo(
                   length / 4.toFloat(), -height.toFloat(),
                   length / 2.toFloat(), 0f
               )
               path.rQuadTo(
                   length / 4.toFloat(),
                   height.toFloat(),
                   length / 2.toFloat(), 0f
               )
               i += length

       }
        if (isClose){
            path.rLineTo(0F,mHeight/2)
            path.rLineTo(-(mWidth*2+2*length),0F)
            path.close()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!isInit) {
            isInit = true
            mWidth = measuredWidth.toFloat()
            mHeight = measuredHeight.toFloat()

            waveLength = (mWidth / 3).toInt()

            // 初始化 小船的浪路径

            // 初始化 小船的浪路径
            initPath(mBoatWavePath, waveLength, boatWaveHeight, true, 2f)

            // 初始化 浪的路径

            // 初始化 浪的路径
            initPath(mWavePath, waveLength, waveHeight, true, 2f)

            // 初始化 小船的路径

            // 初始化 小船的路径
            initPath(mBoatPath, waveLength, boatWaveHeight, false, 1f)

            // 让 PathMeasure 与 Path 关联

            // 让 PathMeasure 与 Path 关联
            mBoatPathMeasure.setPath(mBoatPath, false)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        drawCoordinate(canvas!!)
        val length = mBoatPathMeasure.length
        //画小船运动
        mBoatPathMeasure.getMatrix(
            length * mCurValue,
            mMatrix,
            PathMeasure.POSITION_MATRIX_FLAG or PathMeasure.TANGENT_MATRIX_FLAG
        )
        mMatrix!!.preTranslate(
            -mBoatBitmap!!.width / 2.toFloat(),
            -mBoatBitmap!!.height * 5 / 6.toFloat()
        )

        canvas.drawBitmap(mBoatBitmap!!, mMatrix!!, null)

        // 画船的浪花

        // 画船的浪花
        canvas.save()
        canvas.translate(-mBoatWaveOffset.toFloat(), 0f)
        mWavePaint.color = mBoatBlue
        canvas.drawPath(mBoatWavePath, mWavePaint)
        canvas.restore()

        // 画浪花

        // 画浪花
        canvas.save()
        canvas.translate(-mCurWaveOffset.toFloat(), 0f)
        mWavePaint.color = mWaveBlue
        canvas.drawPath(mWavePath, mWavePaint)
        canvas.restore()
    }

    fun startAnim() {
        mAnimator!!.start()
    }

    fun stopAnim() {
        mAnimator!!.cancel()
    }
}