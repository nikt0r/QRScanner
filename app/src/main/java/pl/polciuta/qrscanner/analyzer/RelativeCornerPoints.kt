package pl.polciuta.qrscanner.analyzer

import android.graphics.Point
import android.graphics.PointF
import android.util.Size

class RelativeCornerPoints(points: Array<Point>, imageSize: Size) {

    private val relativeCorners = points.map { point ->
        PointF(1f * point.x / imageSize.height, 1f * point.y / imageSize.width)
    }

    val xMin = relativeCorners.minOf { it.x }
    val xMax = relativeCorners.maxOf { it.x }
    val yMin = relativeCorners.minOf { it.y }
    val yMax = relativeCorners.maxOf { it.y }

    fun upscale(scaleX: Int, scaleY: Int) = upscale(scaleX.toFloat(), scaleY.toFloat())

    fun upscale(scaleX: Float, scaleY: Float) = relativeCorners.map {
        PointF(it.x * scaleX, it.y * scaleY)
    }

}