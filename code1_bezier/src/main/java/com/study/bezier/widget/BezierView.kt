package com.study.bezier.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import com.study.bezier.ui.BezierActivity
import com.study.bezier.utls.BezierUtils
import com.study.lib_base.BaseView

/**
 * @author dengdai
 * @date 2020/3/31.
 * GitHub：
 * email：291996307@qq.com
 * description：
 */
class BezierView : BaseView {
    // 默认的控制点
    private var DEFAULT_POINT: ArrayList<PointF>? = null

    // 有效触碰的范围
    private var mTouchRegionWidth = 0

    // 当前状态
    private var mState = PREPARE

    // 普通线的宽度
    private var LINE_WIDTH = 3.dpToPx()

    // 贝塞尔曲线的宽度
    private var BEZIER_LINE_WIDTH = 4.dpToPx()

    // 控制点的半径
    private var POINT_RADIO_WIDTH = 5.dpToPx()

    // 速率，每次绘制跳过的帧数，等于10，即表示每次绘制跳过10帧
    private var mRate = 10

    // 绘制贝塞尔曲线的画笔
    private var mBezierPaint: Paint? = null

    // 贝塞尔曲线的路径
    private var mBezierPath: Path? = null

    // 控制点的画笔
    private var mControlPaint: Paint? = null

    // 绘制端点的画笔
    private var mPointPaint: Paint? = null

    // 中间阶层的线画笔
    private var mIntermediatePaint: Paint? = null

    // 绘字笔
    private var mTextPaint: Paint? = null

    // 当前的比例
    private var mCurRatio = 0f

    // 控制点的坐标
    private var mControlPointList: ArrayList<PointF>? = null

    // 贝塞尔曲线的路径点
    private var mBezierPointList: MutableList<PointF>? = null

    // 色值，每一阶的色值
    private val mLineColor: IntArray? by lazy {
        intArrayOf(
            Color.parseColor("#f4ea2a"),//黄色
            Color.parseColor("#1afa29"),//绿色
            Color.parseColor("#13227a"),//蓝色
            Color.parseColor("#515151"),//黑色
            Color.parseColor("#efb336"),//橙色
            Color.parseColor("#e89abe") //粉色
        )
    }
    private var mHandler: Handler? = null

    // 最高阶的控制点个数
    private var mPointCount = 0

    // 是否绘制降阶线
    private var mIsShowReduceOrderLine = false

    // 是否循环播放
    private var mIsLoop = false

    /**
     * 层级说明：
     * 第1层list.存放每一阶的值
     * 即：mIntermediateList.get(0) 即为第(n-1)阶的贝塞尔曲线的数值
     * mIntermediateList.get(1) 即为第(n-2)阶的贝塞尔曲线的数值
     * 第2层list.存放该阶的每条边的数据
     * 第3层list.存放这条边点的数据
     */
    private val mIntermediateList: MutableList<List<List<PointF>>> =
        ArrayList()

    /**
     * 层级说明：
     * 第1层：边的数据
     * 第2层：边中的点数据
     */
    private val mIntermediateDrawList: MutableList<List<PointF>> =
        ArrayList()

    // 绘制时，贝塞尔曲线的点
    private var mCurBezierPoint: PointF? = null

    // 当前选中的点
    private var mCurSelectPoint: PointF? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 设置速率
     *
     * @param rate 速率
     */
    fun setRate(rate: Int) {
        mRate = rate
    }

    /**
     * 设置阶 [2-7]
     *
     * @param order
     */
    fun setOrder(order: Int) {
        mPointCount = order + 1
        mControlPointList!!.clear()
        for (i in 0 until mPointCount) {
            if (i >= DEFAULT_POINT!!.size) {
                break
            }
            mControlPointList!!.add(DEFAULT_POINT!![i])
        }
    }

    /**
     * 设置是否显示降阶线
     *
     * @param isShowReduceOrderLine
     */
    fun setIsShowReduceOrderLine(isShowReduceOrderLine: Boolean) {
        mIsShowReduceOrderLine = isShowReduceOrderLine
    }

    fun isShowReduceOrderLine(): Boolean {
        return mIsShowReduceOrderLine
    }
    fun setIsLoop(isLoop: Boolean) {
        mIsLoop = isLoop
    }

    fun isLoop(): Boolean {
        return mIsLoop
    }

    fun getState(): Int {
        return mState
    }

    fun setCurRatio(curRatio: Float) {
        mCurRatio = curRatio
    }
    override fun init(context: Context?) {
        // 初始化为准备状态
        mState = PREPARE
        mHandler = MyHandler(this)


        val width = context!!.resources.displayMetrics.widthPixels
        DEFAULT_POINT = ArrayList()//坐标点
        DEFAULT_POINT!!.add(PointF((width / 5).toFloat(), (width / 5).toFloat()))
        DEFAULT_POINT!!.add(PointF((width / 3).toFloat(), (width / 2).toFloat()))
        DEFAULT_POINT!!.add(PointF((width / 3 * 2).toFloat(), (width / 4).toFloat()))
        DEFAULT_POINT!!.add(PointF((width / 2).toFloat(), (width / 3).toFloat()))
        DEFAULT_POINT!!.add(PointF((width / 4 * 2).toFloat(), (width / 8).toFloat()))
        DEFAULT_POINT!!.add(PointF((width / 5 * 4).toFloat(), (width / 12).toFloat()))
        DEFAULT_POINT!!.add(PointF((width / 5 * 4).toFloat(), width.toFloat()))
        DEFAULT_POINT!!.add(PointF((width / 2).toFloat(), width.toFloat()))

        // 初始化 控制点
        mControlPointList = ArrayList()
        mPointCount = 8
        for (i in 0 until mPointCount) {
            if (i >= DEFAULT_POINT!!.size) {
                break
            }
            mControlPointList!!.add(DEFAULT_POINT!![i])
        }

        // 初始化贝塞尔的路径的画笔
        mBezierPaint = Paint()
        mBezierPaint!!.isAntiAlias = true
        mBezierPaint!!.color = getBezierLineColor()
        mBezierPaint!!.strokeWidth = BEZIER_LINE_WIDTH
        mBezierPaint!!.style = Paint.Style.STROKE
        mBezierPaint!!.strokeCap = Paint.Cap.ROUND

        // 初始 控制点画笔
        mControlPaint = Paint()
        mControlPaint!!.isAntiAlias = true
        mControlPaint!!.color = getControlLineColor()
        mControlPaint!!.strokeWidth = LINE_WIDTH

        // 初始化 端点画笔
        mPointPaint = Paint()
        mPointPaint!!.isAntiAlias = true
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mTextPaint!!.textSize = 15f

        // 初始化中间阶级的画笔
        mIntermediatePaint = Paint()
        mIntermediatePaint!!.isAntiAlias = true
        mIntermediatePaint!!.strokeWidth = LINE_WIDTH

        // 初始化存放贝塞尔曲线最终结果的路径
        mBezierPath = Path()

        // 触碰范围
        mTouchRegionWidth = 20
    }

    fun start() {

        // 重置 贝塞尔曲线结果 的路径
        mBezierPath!!.reset()

        // 状态至为运行中
        mState = RUNNING

        // 计算 贝塞尔曲线结果 的每个点
        mBezierPointList =
            BezierUtils.buildBezierPoint(mControlPointList, FRAME)
        // 将计算好的 贝塞尔曲线的点 组装成路径
        prepareBezierPath()
        if (mIsShowReduceOrderLine) {
            // 计算 中间阶级的控制点
            BezierUtils.calculateIntermediateLine(
                mIntermediateList,
                mControlPointList,
                FRAME
            )
        }
        mCurRatio = 0f
        setCurBezierPoint(mBezierPointList!![0])
        invalidate()
    }

    /**
     * 暂停 或 继续
     */
    fun pause() {
        if (mState == RUNNING) {
            mState = STOP
        } else if (mState == STOP) {
            mState = RUNNING
            mHandler!!.sendEmptyMessage(HANDLE_EVENT)
        }
    }

    override fun onDraw(canvas: Canvas) {
        // 画坐标和网格
        drawCoordinate(canvas)

        // 绘制控制基线和点
        drawControlLine(canvas)

        // 绘制贝塞尔曲线
        canvas.drawPath(mBezierPath!!, mBezierPaint!!)
        if (mState != PREPARE) {
            mPointPaint!!.style = Paint.Style.FILL
            if (mIsShowReduceOrderLine) {
                // 画中间阶层的线
                for (i in mIntermediateDrawList.indices) {
                    val lineList = mIntermediateDrawList[i]
                    mIntermediatePaint!!.color = getColor(i)
                    mPointPaint!!.color = getColor(i)
                    for (j in 0 until lineList.size - 1) {

                        // 画线
                        canvas.drawLine(
                            lineList[j].x,
                            lineList[j].y,
                            lineList[j + 1].x,
                            lineList[j + 1].y,
                            mIntermediatePaint!!
                        )

                        // 画点
                        canvas.drawCircle(
                            lineList[j].x,
                            lineList[j].y,
                            POINT_RADIO_WIDTH,
                            mPointPaint!!
                        )
                    }
                    canvas.drawCircle(
                        lineList[lineList.size - 1].x,
                        lineList[lineList.size - 1].y,
                        POINT_RADIO_WIDTH,
                        mPointPaint!!
                    )
                }
            }
            mPointPaint!!.color = getBezierLineColor()
            canvas.drawCircle(
                mCurBezierPoint!!.x,
                mCurBezierPoint!!.y,
                POINT_RADIO_WIDTH,
                mPointPaint!!
            )
            mHandler!!.sendEmptyMessage(HANDLE_EVENT)
        }
        if (mCurRatio == 1f && !mIsLoop && context is BezierActivity) {
            (context as BezierActivity).resetPlayBtn()
        }
        canvas.drawText("u = $mCurRatio", mWidth / 4, mHeight * 11 / 12, mTextPaint!!)
    }

    /**
     * 绘制 控制基线 和 点
     */
    private fun drawControlLine(canvas: Canvas) {
        mPointPaint!!.color = getControlLineColor()

        // 绘制 控制点
        for (point in mControlPointList!!) {
            mPointPaint!!.style = Paint.Style.FILL
            mPointPaint!!.strokeWidth = 0f
            canvas.drawCircle(point.x, point.y, POINT_RADIO_WIDTH, mPointPaint!!)
            mPointPaint!!.style = Paint.Style.STROKE
            mPointPaint!!.strokeWidth = 1f
            canvas.drawCircle(point.x, point.y, POINT_RADIO_WIDTH + 2.toFloat(), mPointPaint!!)
        }

        // 绘制第 n 阶的控制基线
        for (i in 0 until mControlPointList!!.size - 1) {
            canvas.drawLine(
                mControlPointList!![i].x,
                mControlPointList!![i].y,
                mControlPointList!![i + 1].x,
                mControlPointList!![i + 1].y,
                mControlPaint!!
            )
        }
    }

    /**
     * 将计算好的 贝塞尔曲线的点 组装成路径
     * 至于这路径中有多少个点，取决于[.FRAME]属性的值
     */
    private fun prepareBezierPath() {
        for (i in mBezierPointList!!.indices) {
            val point = mBezierPointList!![i]
            if (i == 0) {
                mBezierPath!!.moveTo(point.x, point.y)
            } else {
                mBezierPath!!.lineTo(point.x, point.y)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        // 没有在准备状态不能进行操作
        if (mState != PREPARE) {
            return true
        }

        // 触碰的坐标
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> isLegalControlPoint(x, y)
            MotionEvent.ACTION_MOVE -> {
                if (mCurSelectPoint == null) {
                    return true
                }
                mCurSelectPoint!!.x = x
                mCurSelectPoint!!.y = y
                mIntermediateList.clear()
                mIntermediateDrawList.clear()
                if (mBezierPointList != null) {
                    mBezierPointList!!.clear()
                }
                mBezierPath!!.reset()
                invalidate()
            }
            MotionEvent.ACTION_UP -> mCurSelectPoint = null
        }
        return true
    }

    /**
     * 获取 触碰点 范围内有效的 控制点
     *
     * @param x
     * @param y
     */
    private fun isLegalControlPoint(x: Float, y: Float) {
        if (mCurSelectPoint == null) {
            for (controlPoint in mControlPointList!!) {
                val pointRange = RectF(
                    controlPoint.x - mTouchRegionWidth,
                    controlPoint.y - mTouchRegionWidth,
                    controlPoint.x + mTouchRegionWidth,
                    controlPoint.y + mTouchRegionWidth
                )

                // 如果包含了就，返回true
                if (pointRange.contains(x, y)) {
                    mCurSelectPoint = controlPoint
                    return
                }
            }
        }
    }

    /**
     * 获取控制线、控制点的色值
     *
     * @return
     */
    private fun getControlLineColor(): Int {
        return getColor("#1296db")
    }

    /**
     * 获取贝塞尔曲线的色值
     *
     * @return
     */
    private fun getBezierLineColor(): Int {
        return getColor("#d81e06")
    }

    /**
     * 获取 [.mLineColor] 的对应下标色值，如果越界则取余
     *
     * @param index 色值下标
     * @return
     */
    private fun getColor(index: Int): Int {
        return mLineColor!![index % mLineColor!!.size]
    }

    /**
     * 获取颜色
     *
     * @param color 色值，格式：#xxxxxx
     * @return
     */
    private fun getColor(color: String): Int {
        return Color.parseColor(color)
    }

    /**
     * 设置绘制值
     *
     * @param intermediateDrawList
     */
    private fun setIntermediateDrawList(intermediateDrawList: List<List<PointF>>) {
        mIntermediateDrawList.clear()
        mIntermediateDrawList.addAll(intermediateDrawList)
    }

    private fun setState(state: Int) {
        mState = state
    }

    private fun getRate(): Int {
        return mRate
    }

    private fun getSize(): Int {
        return mBezierPointList!!.size
    }

    private fun setCurBezierPoint(curBezierPoint: PointF) {
        mCurBezierPoint = curBezierPoint
    }

    private fun getBezierPointList(): List<PointF>? {
        return mBezierPointList
    }

    private fun getIntermediateList(): List<List<List<PointF>>> {
        return mIntermediateList
    }

    private class MyHandler(// 贝塞尔曲线的视图
        private val mView: BezierView
    ) : Handler() {

        // 当前帧数
        private var mCurFrame = 0
        override fun handleMessage(msg: Message) {
            if (msg.what == HANDLE_EVENT) {

                // 按了 暂停，则不在进行
                if (mView.getState() == STOP) {
                    return
                }

                // 增加 帧数，让线移动起来
                mCurFrame += mView.getRate()
                // 当帧数超出界限则不在运行，让当前 帧数 和 状态复位，并且清空降阶线的数据
                if (mCurFrame >= mView.getSize()) {
                    mCurFrame = 0
                    if (!mView.mIsLoop) {
                        mView.setState(PREPARE)
                        mView.setIntermediateDrawList(ArrayList())
                        mView.setCurRatio(1f)
                        mView.invalidate()
                        return
                    }
                }

                // 获取当前的贝塞尔曲线点
                val bezierPointList = mView.getBezierPointList()
                mView.setCurBezierPoint(bezierPointList!![mCurFrame])

                // 是否要显示 降阶线
                if (mView.isShowReduceOrderLine()) {
                    val intermediateList =
                        mView.getIntermediateList()

                    // 实时变动的线
                    val intermediateDrawList: MutableList<List<PointF>> =
                        ArrayList()
                    for (i in intermediateList.indices) {
                        val lineList =
                            intermediateList[i]
                        val intermediatePoint: MutableList<PointF> =
                            ArrayList()
                        for (j in lineList.indices) {
                            val x = lineList[j][mCurFrame].x
                            val y = lineList[j][mCurFrame].y
                            intermediatePoint.add(PointF(x, y))
                        }
                        intermediateDrawList.add(intermediatePoint)
                    }
                    mView.setIntermediateDrawList(intermediateDrawList)
                }
                val ratio :Float= (mCurFrame.toFloat() / mView.getSize() * 100).toInt() / 100f
                mView.setCurRatio((if (ratio > 1F) 1F else ratio))

                // 刷新视图
                mView.invalidate()
            }
        }

    }

    companion object {
        // 帧数：1000，即1000个点来绘制一条线
        private const val FRAME = 1000

        // handler 事件
        private const val HANDLE_EVENT = 12580

        // 准备状态
        const val PREPARE = 0x0001

        // 运行状态
        const val RUNNING = 0x0002

        // 停止状态
        const val STOP = 0x0004
    }
}