package pl.polciuta.qrscanner.animation

import android.graphics.*
import android.view.SurfaceHolder
import pl.polciuta.qrscanner.config.HighlightColor


class Reticle(private val overlaySurfaceHolder: SurfaceHolder, private val colors: Map<HighlightColor, Int>) {

    private val reticlePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = colors[HighlightColor.RETICLE] ?: Color.YELLOW
            style = Paint.Style.STROKE
            strokeWidth = RETICLE_STROKE_WIDTH
            strokeCap = Paint.Cap.SQUARE
            alpha = RETICLE_ALPHA
        }
    }

    private val eraserPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = ERASER_STROKE_WIDTH
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            style = Paint.Style.FILL_AND_STROKE
        }
    }

    // options:
    // scale - x, y
    // padding - n

    fun drawShape(points: List<PointF>, padding: Int = DEFAULT_SHAPE_PADDING) {
        overlaySurfaceHolder.apply {
            lockCanvas()?.apply {
                drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                colors[HighlightColor.BACKGROUND]?.let { drawColor(it) }

                val shapePath = makeShapePath(points, padding)
                val reticlePath = makeReticlePath(points, padding + RETICLE_PADDING)

                drawPath(reticlePath, reticlePaint)
                drawPath(shapePath, eraserPaint)

                unlockCanvasAndPost(this)
            }
        }
    }

    fun clearOverlay() {
        overlaySurfaceHolder.apply {
            lockCanvas()?.apply {
                drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                unlockCanvasAndPost(this)
            }
        }
    }

    private fun makeShapePath(fPoints: List<PointF>, padding: Int) =
        Path().apply {
            moveTo(fPoints[0].x - padding, fPoints[0].y - padding)
            lineTo(fPoints[1].x + padding, fPoints[1].y - padding)
            lineTo(fPoints[2].x + padding, fPoints[2].y + padding)
            lineTo(fPoints[3].x - padding, fPoints[3].y + padding)
            lineTo(fPoints[0].x - padding, fPoints[0].y - padding)
        }

    private fun makeReticlePath(fPoints: List<PointF>, padding: Int) =
        Path().apply {
            val m = RETICLE_HAND_FRACTION
            val n = 1f - m

            val x1 = fPoints[0].x - padding
            val y1 = fPoints[0].y - padding
            val x2 = fPoints[1].x + padding
            val y2 = fPoints[1].y - padding
            val x3 = fPoints[2].x + padding
            val y3 = fPoints[2].y + padding
            val x4 = fPoints[3].x - padding
            val y4 = fPoints[3].y + padding

            moveTo(x1, y1)
            makeSubpath(m, n, x1, y1, x2, y2)
            makeSubpath(m, n, x2, y2, x3, y3)
            makeSubpath(m, n, x3, y3, x4, y4)
            makeSubpath(m, n, x4, y4, x1, y1)
        }

    // Formula used: internal line division (divide line internally in the ratio m:n)
    private fun Path.makeSubpath(m: Float, n: Float, x1: Float, y1: Float, x2: Float, y2: Float) {
        lineTo(calcMid(m, x2, n, x1), calcMid(m, y2, n, y1))
        moveTo(calcMid(n, x2, m, x1), calcMid(n, y2, m, y1))
        lineTo(x2, y2)
    }

    // Value between values value1 and value2 in the ratio m:n
    private fun calcMid(m: Float, value2: Float, n: Float, value1: Float) = (m * value2 + n * value1)

    companion object {
        private const val RETICLE_STROKE_WIDTH = 6f
        private const val RETICLE_ALPHA = 90
        private const val RETICLE_HAND_FRACTION = .2f
        private const val ERASER_STROKE_WIDTH = 2f
        private const val DEFAULT_SHAPE_PADDING = 20
        private const val RETICLE_PADDING = 20
    }
}