package pl.polciuta.qrscanner.utils

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import kotlin.math.max
import kotlin.math.min

object BitmapUtils {

    // forSize - if bitmap size differs from forSize, rescale corners and border accordingly
    fun getRoundedCornerBitmap(bitmap: Bitmap, color: Int, cornerSizePx: Int, borderSizePx: Int, forSize: Int): Bitmap {
        val scale = 1.0f * bitmap.height / forSize
        val corner = cornerSizePx * scale
        val border = borderSizePx * scale
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        // prepare canvas for transfer
        paint.isAntiAlias = true
        paint.color = -0x1
        paint.style = Paint.Style.FILL
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, corner, corner, paint)

        // draw bitmap
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        // draw border
        paint.color = color
        paint.alpha = 0x90
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = border
        canvas.drawRoundRect(rectF, corner, corner, paint)
        return output
    }

    // Return square or horizontal rectangle (w >= h)
    fun getBitmapSquareAtLeast(bitmap: Bitmap, cornerPoints: RelativeCornerPoints, padding: Int = 0): Bitmap? {

        return bitmap.let {
//            val it = stretchBitmap(cornerPoints, bitmap)
            var left = (1f * cornerPoints.xMin * it.width - padding).toInt()
            var right = (1f * cornerPoints.xMax * it.width + padding).toInt()
            var top = (1f * cornerPoints.yMin * it.height - padding).toInt()
            var bottom = (1f * cornerPoints.yMax * it.height + padding).toInt()
            val w = right - left
            val h = bottom - top
            when {
//                w > h -> {
//                    top -= (w - h) / 2
//                    bottom += (w - h) / 2
//                }
                h > w -> {
                    left -= (h - w) / 2
                    right += (h - w) / 2
                }
            }

            if (left < 0) {
//                right -= left // left is negative hence subtraction
                right = min(right - left, it.width) // left is negative hence subtraction
                left = 0
            }
            if (right > it.width) {
//                left -= right - it.width
                left = max(0, left - (right - it.width))
                right = it.width
            }

            if (top < 0) {
//                bottom -= top // top is negative hence subtraction
                bottom = min(bottom - top, it.height) // top is negative hence subtraction
                top = 0
            }
            if (bottom > it.height) {
//                top -= bottom - it.height
                top = max(0, top - (bottom - it.height))
                bottom = it.height
            }

            val width = right - left
            val height = bottom - top

            Bitmap.createBitmap(it, left, top, width, min(width, height))
//            stretchBitmap(cornerPoints, Bitmap.createBitmap(it, left, top, width, min(width, height)))
        }

    }

    fun convertDpToPx(cornerDips: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, cornerDips,
            context.resources.displayMetrics
        )
    }

}