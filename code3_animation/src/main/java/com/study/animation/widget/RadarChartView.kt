package com.study.animation.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Paint.Align
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * @author Jiang zinc
 * @date 创建时间：2019/1/15
 * @description 雷达图
 */
open class RadarChartView : View {
    // 边框宽度
    private val RADAR_BORDER_LINE_WIDTH = dpToPx(0.5f).toFloat()

    // 维度分割线宽度
    private val RADAR_DIMEN_LINE_WIDTH = dpToPx(0.5f).toFloat()

    // 数据线宽度
    private val DATA_LINE_WIDTH = dpToPx(1.5f).toFloat()

    // 小点的半径
    private val DOT_RADIUS = dpToPx(1.5f).toFloat()

    // 字体大小
    private val TEXT_SIZE = spToPx(10f).toFloat()

    // 纬度数
    private var mDimenCount = 0

    // 每个纬度的线长
    private var mLength = 0f

    // 每个纬度角度
    private var mAngle = 0.0

    // 雷达图 顶点坐标集
    private var mVertexList: MutableList<PointF>? = null

    // 雷达图 线的画笔
    private var mLinePaint: Paint? = null

    // 雷达图 背景画笔
    private var mRadarBgPaint: Paint? = null

    // 绘制数据的画笔
    private var mDataPaint: Paint? = null

    // 绘制文字的画笔
    private var mTextPaint: Paint? = null

    // 雷达图 边框路径
    private var mRadarLinePath: Path? = null

    // 雷达图 维度分割线
    private var mDimensionPath: Path? = null

    // 雷达图 背景路径
    private var mRadarBgPath: Path? = null

    // 雷达图 正在运动的路径
    private var mRunningPath: Path? = null

    // 雷达图的中心点
    private var mRadarCenterPoint: PointF? = null

    // 雷达图数据
    private var mDataList: MutableList<Data>? = null

    // 雷达图基础数据
    private var mBaseDataList: MutableList<Data>? = null

    // 文字描述
    private var mTextDataList: MutableList<String>? = null

    // 插值器
    private var mAnimator: ValueAnimator? = null

    // 当前的插值器值
    private var mAnimCurValue = 0f

    // 总共需要的循环数
    private var mTotalLoopCount = 0

    // 当前需要的循环数
    private var mCurLoopCount = 0

    // 当前动画状态
    private var mCurState = 0
    protected var mWidth = 0f
    protected var mHeight = 0f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        mLength = min(w, h) / 4.toFloat()
        calculateRadarChartVertex()
        initRadarLine()
    }

    init {
        mCurState = INIT
        mVertexList = ArrayList()
        mLinePaint = Paint()
        mLinePaint!!.isAntiAlias = true
        mLinePaint!!.color = Color.parseColor(DEFAULT_LINE_COLOR)
        // 连线圆角
        mLinePaint!!.pathEffect = CornerPathEffect(dpToPx(2.5f).toFloat())
        mRadarBgPaint = Paint()
        mRadarBgPaint!!.isAntiAlias = true
        mRadarBgPaint!!.style = Paint.Style.FILL
        mRadarBgPaint!!.pathEffect = CornerPathEffect(dpToPx(2.5f).toFloat())
        mDataPaint = Paint()
        mDataPaint!!.isAntiAlias = true
        mDataPaint!!.strokeWidth = DATA_LINE_WIDTH
        mDataPaint!!.pathEffect = CornerPathEffect(dpToPx(1f).toFloat())
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textSize = TEXT_SIZE
        mTextPaint!!.color = Color.parseColor(DEFAULT_LINE_COLOR)
        mRadarLinePath = Path()
        mDimensionPath = Path()
        mRadarBgPath = Path()
        mRunningPath = Path()
        mDataList = ArrayList()
        mBaseDataList = ArrayList()
        mTextDataList = ArrayList()
        mRadarCenterPoint = DEFAULT_CENTER_POINT
        mDimenCount = DEFAULT_DIMEN_COUNT
        mAngle = CIRCLE_ANGLE / mDimenCount
    }

    /**
     * 设置文字描述数据
     *
     * @param textDataList
     */
    fun setTextDataList(textDataList: List<String>?) {
        if (mCurState != INIT) {
            Log.w(TAG, "Cancel or stop the animation first.")
            return
        }
        mTextDataList!!.clear()
        mTextDataList!!.addAll(textDataList!!)
    }

    /**
     * 设置维度
     *
     * @param dimenCount
     */
    @Suppress("NAME_SHADOWING")
    fun setDimenCount(dimenCount: Int) {
        var dimenCount = dimenCount
        if (dimenCount < 3) {
            Log.w(
                TAG,
                "Dimension is must be bigger than two."
            )
            dimenCount = 3
        }
        if (mCurState != INIT) {
            Log.w(TAG, "Cancel or stop the animation first.")
            return
        }
        mBaseDataList!!.clear()
        mDataList!!.clear()

        // 设置维度，并重新计算每个维度角度
        mDimenCount = dimenCount
        mAngle = CIRCLE_ANGLE / mDimenCount
        calculateRadarChartVertex()
        initRadarLine()
        invalidate()
    }

    /**
     * 设置数据
     *
     * @param dataList
     */
    fun setDataList(dataList: List<Data>?) {
        if (mCurState != INIT) {
            Log.w(TAG, "Cancel or stop the animation first.")
            return
        }
        mDataList!!.clear()
        mDataList!!.addAll(dataList!!)
    }

    /**
     * 设置基本数据
     *
     * @param baseDataList
     */
    fun setBaseDataList(baseDataList: List<Data>?) {
        if (mCurState != INIT) {
            Log.w(TAG, "Cancel or stop the animation first.")
            return
        }
        mBaseDataList!!.clear()
        mBaseDataList!!.addAll(baseDataList!!)
    }

    /**
     * 检测数据中的 [data] 长度是否和 [.mDimenCount] 相同
     *
     * @param dataList 数据
     */
    private fun checkData(dataList: List<Data>?) {
        for (i in dataList!!.indices) {
            if (dataList[i].data.size != mDimenCount) {
                throw RuntimeException("The Data size is not equal to dimension count.")
            }
        }
    }

    /**
     * 开始绘制
     */
    fun start() {
        if (mCurState != INIT) {
            Log.w(TAG, "Cancel or stop the animation first.")
            return
        }
        mCurState = RUNNING

        // 检测基线数据
        checkData(mBaseDataList)
        // 检测数据
        checkData(mDataList)
        // 检测文字描述数据
        if (mTextDataList!!.size != 0 && mTextDataList!!.size != mDimenCount) {
            throw RuntimeException("Text data length should be zero or equal with dimension count.")
        }

        // 将当前循环数置为 0
        mCurLoopCount = 0

        // 计算数据
        calculateDataVertex(true)
        calculateDataVertex(false)
        /**
         * 第一个维度不需要展开，所以维度数需要-1
         * 这里不使用 setRepeatMode 设置多次循环,
         * 是因为 [AnimatorListenerAdapter.onAnimationRepeat] 和
         * [ValueAnimator.AnimatorUpdateListener.onAnimationUpdate]
         * 无法确保其顺序，有时会出现乱值现象，这种现象目前有概率出现在 mate10（8.1.0）手机上，所以使用这种方法进行规避
         */
        mTotalLoopCount = (mDimenCount - 1) * mDataList!!.size
        mAnimator = ValueAnimator.ofFloat(0f, mTotalLoopCount.toFloat())
        mAnimator!!.duration = DURATION * mTotalLoopCount.toLong()
        mAnimator!!.interpolator = LinearInterpolator()
        mAnimator!!.addUpdateListener { animation ->
            val value = animation.animatedValue as Float

            // 整数部分即为当前的动画数据下标
            mCurLoopCount = value.toInt()

            // 小数部分极为当前维度正在展开的进度百分比
            mAnimCurValue = value - mCurLoopCount
            invalidate()
        }
        mAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // 动画结束，将状态置为初始状态，并再刷新一次，让最后的数据全部显示
                mCurState = INIT
                invalidate()
            }
        })

        // 开启动画
        mAnimator!!.start()
    }

    /**
     * 停止动画
     */
    fun stop() {
        if (mAnimator == null) {
            return
        }
        mAnimator!!.cancel()
    }

    /**
     * 清空数据
     */
    fun reset() {
        if (mAnimator != null) {
            mAnimator!!.cancel()
        }
        mBaseDataList!!.clear()
        mDataList!!.clear()
        invalidate()
    }

    /**
     * 计算数据的顶点坐标
     *
     * @param isBase 是否为 基础数据
     */
    private fun calculateDataVertex(isBase: Boolean) {
        val calDataList: List<Data>? =
            if (isBase) mBaseDataList else mDataList
        for (i in calDataList!!.indices) {
            val data = calDataList[i]

            // 获取 比例数据
            val pointDataList = data.data

            // 设置路径
            val curPath = Path()
            data.setPath(curPath)
            curPath.reset()
            for (j in pointDataList.indices) {

                // 当前维度的数据比例
                val ratio = pointDataList[j]
                // 当前维度的顶点坐标
                val curDimenPoint = mVertexList!![j]
                if (j == 0) {
                    curPath.moveTo(
                        curDimenPoint.x * ratio,
                        curDimenPoint.y * ratio
                    )
                } else {
                    curPath.lineTo(
                        curDimenPoint.x * ratio,
                        curDimenPoint.y * ratio
                    )
                }
            }
            curPath.close()
        }
    }

    /**
     * 计算雷达图的顶点，这里只是计算，没有进行路径拼凑
     * [.initRadarLine]进行拼凑路径
     */
    private fun calculateRadarChartVertex() {

        // 清除之前顶点
        mVertexList!!.clear()

        // 循环遍历计算顶点坐标
        for (i in 0 until mDimenCount) {
            val point = PointF()

            // 当前角度
            val curAngle = i * mAngle
            // 转弧度制
            val radian = Math.toRadians(curAngle)

            // 计算其 x、y 的坐标
            // y轴需要进行取反，因为canvas的坐标轴和我们数学中的坐标轴的y轴正好是上下相反的
            point.x = (mLength * sin(radian)).toFloat()
            point.y = (-(mLength * cos(radian))).toFloat()
            mVertexList!!.add(point)
        }
    }

    /**
     * 初始化 雷达图 外边框和维度分割线
     */
    private fun initRadarLine() {
        // 先清空
        mRadarLinePath!!.reset()
        mDimensionPath!!.reset()

        // 画 外边框
        for (i in mVertexList!!.indices) {
            if (i == 0) {
                mRadarLinePath!!.moveTo(mVertexList!![i].x, mVertexList!![i].y)
            } else {
                mRadarLinePath!!.lineTo(mVertexList!![i].x, mVertexList!![i].y)
            }
        }
        mRadarLinePath!!.close()

        // 维度分割线
        for (i in mVertexList!!.indices) {
            mDimensionPath!!.moveTo(mVertexList!![i].x, mVertexList!![i].y)
            mDimensionPath!!.lineTo(mRadarCenterPoint!!.x, mRadarCenterPoint!!.y)
        }
    }

    override fun onDraw(canvas: Canvas) {

        // 平移画布至中心
        canvas.translate(mWidth / 2, mHeight / 2)

        // 画雷达 框和维度线
        drawRadarLine(canvas)
        // 画雷达 背景
        drawRadarBackground(canvas)
        // 画 顶点的小点
        drawDot(canvas)
        // 画文字
        drawText(canvas)

        // 画基线数据
        drawData(canvas, true)
        if (mCurState == INIT) {
            drawData(canvas, false)
        } else {
            drawRunningData(canvas)
        }
    }

    /**
     * 画文字
     *
     * @param canvas 画布
     */
    private fun drawText(canvas: Canvas) {
        if (mTextDataList == null || mTextDataList!!.size != mDimenCount) {
            Log.w(
                TAG,
                "The length of description text list is not equal with dimension."
            )
            return
        }
        for (i in 0 until mDimenCount) {
            val vertexPoint = mVertexList!![i]

            // 所在象限
            val dimension = checkThePointDimension(vertexPoint)
            var align: Align
            var y = vertexPoint.y * 1.15f
            when (dimension) {
                1 -> align = Align.CENTER
                2 -> align = Align.LEFT
                3 -> {
                    align = Align.LEFT
                    y -= (mTextPaint!!.descent() + mTextPaint!!.ascent()) / 2
                }
                4 -> {
                    align = Align.LEFT
                    y -= mTextPaint!!.descent() + mTextPaint!!.ascent()
                }
                5 -> {
                    align = Align.CENTER
                    y -= mTextPaint!!.descent() + mTextPaint!!.ascent()
                }
                6 -> {
                    align = Align.RIGHT
                    y -= mTextPaint!!.descent() + mTextPaint!!.ascent()
                }
                7 -> {
                    align = Align.RIGHT
                    y -= mTextPaint!!.descent() + mTextPaint!!.ascent() / 2
                }
                8 -> align = Align.RIGHT
                else -> align = Align.CENTER
            }
            mTextPaint!!.textSize = TEXT_SIZE
            mTextPaint!!.textAlign = align
            canvas.drawText(
                mTextDataList!![i],
                vertexPoint.x * 1.15f,
                y,
                mTextPaint!!
            )
        }
    }

    /**
     * 画 顶点的小点，有描述文字时，才绘制小点
     *
     * @param canvas 画布
     */
    private fun drawDot(canvas: Canvas) {
        if (mTextDataList == null || mTextDataList!!.size != mDimenCount) {
            Log.w(
                TAG,
                "The length of description text list is not equal with dimension."
            )
            return
        }
        for (point in mVertexList!!) {
            mLinePaint!!.style = Paint.Style.FILL
            canvas.drawCircle(point.x * 1.08f, point.y * 1.08f, DOT_RADIUS, mLinePaint!!)
        }
    }

    /**
     * 绘制运动中的数据
     *
     * @param canvas 画布
     */
    private fun drawRunningData(canvas: Canvas) {

        // 数据为空 则不绘制
        if (mDataList!!.size <= 0) {
            return
        }

        // 当前数据的下标（-1因为第一个维度不用动画）
        val curIndex = mCurLoopCount / (mDimenCount - 1)
        // 当前数据的维度（-1因为第一个维度不用动画）
        val curDimen = mCurLoopCount % (mDimenCount - 1) + 1
        for (i in 0..curIndex) {
            var path: Path?

            // 当前对比的数据
            val curData = mDataList!![i]

            // 当前需要进行运动展开的对比
            if (i == curIndex) {
                // 重制运动中的路径
                mRunningPath!!.reset()

                // 第一维度 的 顶点是固定的
                mRunningPath!!.moveTo(
                    curData.data[0] * mVertexList!![0].x,
                    curData.data[0] * mVertexList!![0].y
                )

                // 绘制 2-curDimen 维度
                for (j in 1..curDimen) {

                    // 当前维度的对比数据 所占该维度的比例
                    val curDimenRatio = curData.data[j]

                    // 当前维度的顶点坐标
                    val curDimenVertexPoint = mVertexList!![j]
                    val x = curDimenVertexPoint.x * curDimenRatio
                    val y = curDimenVertexPoint.y * curDimenRatio
                    if (j == curDimen) {
                        // 绘制正在移动的点
                        mRunningPath!!.lineTo(x * mAnimCurValue, y * mAnimCurValue)
                    } else {
                        // 绘制已经固定的点
                        mRunningPath!!.lineTo(x, y)
                    }
                }

                // 不是最后的点则还需连接原点
                if (curDimen != mDimenCount - 1) {
                    mRunningPath!!.lineTo(mRadarCenterPoint!!.x, mRadarCenterPoint!!.y)
                }
                mRunningPath!!.close()
                path = mRunningPath
            } else {
                path = mDataList!![i].getPath()
            }

            // 画轮廓
            mDataPaint!!.style = Paint.Style.STROKE
            mDataPaint!!.color = curData.color
            mDataPaint!!.strokeWidth = DATA_LINE_WIDTH
            canvas.drawPath(path!!, mDataPaint!!)

            // 画背景
            mDataPaint!!.style = Paint.Style.FILL
            mDataPaint!!.color = getAlphaColor(curData.color, 127)
            canvas.drawPath(path, mDataPaint!!)
        }
    }

    /**
     * 画背景
     */
    private fun drawRadarBackground(canvas: Canvas) {
        for (i in 0 until RADAR_BG_SHOW_LEVEL) {
            mRadarBgPath!!.reset()
            for (j in 0 until mDimenCount) {
                val curVertexPoint = mVertexList!![j]
                val x =
                    curVertexPoint.x * (RADAR_BG_LEVEL - i) / RADAR_BG_LEVEL
                val y =
                    curVertexPoint.y * (RADAR_BG_LEVEL - i) / RADAR_BG_LEVEL
                if (j == 0) {
                    mRadarBgPath!!.moveTo(x, y)
                } else {
                    mRadarBgPath!!.lineTo(x, y)
                }
            }
            mRadarBgPath!!.close()
            mRadarBgPaint!!.color = getAlphaColor(
                Color.parseColor(DEFAULT_LINE_COLOR),
                i * BG_ALPHA_LEVEL
            )
            canvas.drawPath(mRadarBgPath!!, mRadarBgPaint!!)
        }
    }

    /**
     * 绘制雷达图的网格线
     */
    private fun drawRadarLine(canvas: Canvas) {

        // 绘制雷达图边框
        mLinePaint!!.style = Paint.Style.STROKE
        mLinePaint!!.strokeWidth = RADAR_BORDER_LINE_WIDTH
        canvas.drawPath(mRadarLinePath!!, mLinePaint!!)

        // 绘制雷达图维度分割线
        mLinePaint!!.style = Paint.Style.STROKE
        mLinePaint!!.strokeWidth = RADAR_DIMEN_LINE_WIDTH
        canvas.drawPath(mDimensionPath!!, mLinePaint!!)
    }

    /**
     * 绘制基线数据
     *
     * @param canvas 画布
     * @param isBase 是否为基线
     */
    private fun drawData(canvas: Canvas, isBase: Boolean) {
        val dataList: List<Data>? =
            if (isBase) mBaseDataList else mDataList
        for (i in dataList!!.indices) {
            val data = dataList[i]
            val color = data.color
            mDataPaint!!.color = color
            mDataPaint!!.style = Paint.Style.STROKE
            if (isBase) {
                mDataPaint!!.strokeWidth = RADAR_BORDER_LINE_WIDTH
            } else {
                mDataPaint!!.strokeWidth = DATA_LINE_WIDTH
            }
            canvas.drawPath(data.getPath(), mDataPaint!!)
            if (!isBase) {
                mDataPaint!!.style = Paint.Style.FILL
                mDataPaint!!.color = getAlphaColor(color, 127)
                canvas.drawPath(data.getPath(), mDataPaint!!)
            }
        }
    }

    /**
     * 给颜色加透明度
     *
     * @param color 颜色
     * @param alpha 透明
     * @return 加了透明度的颜色
     */
    @Suppress("NAME_SHADOWING")
    private fun getAlphaColor(color: Int, alpha: Int): Int {
        var color = color
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        color = Color.HSVToColor(alpha, hsv)
        return color
    }

    /**
     * 获取点所在的象限
     *
     * <pre>
     * ┃
     * ┃
     * ┃
     * 8       1       2
     * ┃
     * ┃
     * ━━━━━7━━━━━━━╋━━━━━━━3━━━━━━▶ x
     * ┃
     * ┃
     * 6       5       4
     * ┃
     * ┃
     * ┃
     * ▼
     * y
    </pre> *
     *
     * @return
     */
    private fun checkThePointDimension(pointF: PointF?): Int {
        if (pointF == null) {
            return -1
        }
        val x = pointF.x.toInt()
        val y = pointF.y.toInt()
        if (x == 0 && y < 0) {
            return 1
        } else if (x > 0 && y < 0) {
            return 2
        } else if (x > 0 && y == 0) {
            return 3
        } else if (x > 0 && y > 0) {
            return 4
        } else if (x == 0 && y > 0) {
            return 5
        } else if (x < 0 && y > 0) {
            return 6
        } else if (x < 0 && y == 0) {
            return 7
        } else if (x < 0 && y < 0) {
            return 8
        }
        return -1
    }

    /**
     * 转换 sp 至 px
     *
     * @param spValue sp值
     * @return px值
     */
    protected fun spToPx(spValue: Float): Int {
        val fontScale =
            Resources.getSystem().displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 转换 dp 至 px
     *
     * @param dpValue dp值
     * @return px值
     */
    private fun dpToPx(dpValue: Float): Int {
        val metrics = Resources.getSystem().displayMetrics
        return (dpValue * metrics.density + 0.5f).toInt()
    }

    /**
     * @author Jiang zinc
     * @date 创建时间：2019/1/16
     * @description 雷达图数据
     */
    class Data(
        /**
         * 雷达图的数据，数据范围 [0-1]
         * 低于0，处理为0
         * 大于1，处理为1
         * 数据长度，要和雷达图维度相同
         */
        val data: List<Float>,
        /**
         * 数据色值
         */
        val color: Int
    ) {

        private var path: Path? = null

        fun setPath(path: Path) {
            this.path = path
        }

        fun getPath(): Path {
            return this.path!!
        }

    }

    companion object {
        private const val TAG = "RadarChartView"

        // 默认6维度
        private const val DEFAULT_DIMEN_COUNT = 6

        // 360度
        private const val CIRCLE_ANGLE = 360.0

        // 网格线的默认颜色
        private const val DEFAULT_LINE_COLOR = "#7a7a7a"

        // 默认中心点
        private val DEFAULT_CENTER_POINT = PointF(0F, 0F)

        // 雷达图 背景渐变 分割的层级
        private const val RADAR_BG_LEVEL = 10

        // 雷达图 背景渐变 显示的层级
        private const val RADAR_BG_SHOW_LEVEL = 6

        // 背景 阶梯 透明
        private const val BG_ALPHA_LEVEL = 10

        // 一个纬度的动画持续时间
        private const val DURATION = 200

        // 初始化状态
        private const val INIT = 0x001

        // 运行状态
        private const val RUNNING = 0x002
    }

}