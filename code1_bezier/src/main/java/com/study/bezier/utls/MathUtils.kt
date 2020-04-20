package com.study.bezier.utls

import android.graphics.PointF
import kotlin.math.*

object MathUtils {
    /**
     * 获得两点之间的直线距离
     *
     * @param p1 PointF
     * @param p2 PointF
     * @return 两点之间的直线距离
     */
    fun getTwoPointDistance(p1: PointF, p2: PointF): Float {
        return sqrt(
            (p1.x - p2.x.toDouble()).pow(2.0) + (p1.y - p2.y.toDouble()).pow(2.0)
        ).toFloat()
    }

    /**
     * 根据两个点(x1,y1)(x2,y2)的坐标算出斜率
     *
     * @param x1 x1
     * @param x2 x2
     * @param y1 y1
     * @param y2 y2
     * @return 斜率
     */
    fun getLineSlope(
        x1: Float,
        x2: Float,
        y1: Float,
        y2: Float
    ): Float? {
        return if (x2 - x1 == 0f) null else (y2 - y1) / (x2 - x1)
    }

    /**
     * 根据传入的两点得到斜率
     *
     * @param p1 PointF
     * @param p2 PointF
     * @return 返回斜率
     */
    fun getLineSlope(p1: PointF, p2: PointF): Float? {
        return if (p2.x - p1.x == 0f) null else (p2.y - p1.y) / (p2.x - p1.x)
    }

    /**
     * Get middle point between p1 and p2.
     * 获得两点连线的中点
     *
     * @param p1 PointF
     * @param p2 PointF
     * @return 中点
     */
    fun getMiddlePoint(p1: PointF, p2: PointF): PointF {
        return PointF((p1.x + p2.x) / 2.0f, (p1.y + p2.y) / 2.0f)
    }

    /**
     * Get the point of intersection between circle and line.
     * 获取 通过指定圆心，斜率为lineK的直线与圆的交点。
     *
     * @param pMiddle The circle center point.
     * @param radius  The circle radius.
     * @param lineK   The slope of line which cross the pMiddle.
     * @return
     */
    fun getIntersectionPoints(
        pMiddle: PointF,
        radius: Float,
        lineK: Float?
    ): Array<PointF?> {
        val points = arrayOfNulls<PointF>(2)
        val radian: Float
        val xOffset: Float
        val yOffset: Float
        if (lineK != null) {
            radian = atan(lineK.toDouble()).toFloat()
            xOffset = (sin(radian.toDouble()) * radius).toFloat()
            yOffset = (cos(radian.toDouble()) * radius).toFloat()
        } else {
            xOffset = radius
            yOffset = 0f
        }
        points[0] = PointF(pMiddle.x + xOffset, pMiddle.y - yOffset)
        points[1] = PointF(pMiddle.x - xOffset, pMiddle.y + yOffset)
        return points
    }
}