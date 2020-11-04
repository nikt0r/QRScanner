package pl.polciuta.qrscanner.utils

import android.content.Context
import android.util.Size
import android.view.Gravity
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import pl.polciuta.qrscanner.card.InfoRow

inline fun Boolean.alsoIfTrue(block: () -> Unit): Boolean {
    if (this) block()
    return this
}

inline fun Boolean.alsoIfFalse(block: () -> Unit): Boolean {
    if (!this) block()
    return this
}

val InputImage.size
    get() = Size(width, height)


object Helpers {

    fun showToast(context: Context, message: String, gravity: Int = Gravity.CENTER, yOffset: Int = 0) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).apply {
            setGravity(gravity, 0, yOffset)
            show()
        }
    }

    fun createRowList(pairList: List<Pair<String, String?>>) =
        pairList.mapNotNull { pair ->
            pair.second?.let { if (it.isNotBlank()) InfoRow(pair.first, it) else null }
        }

}