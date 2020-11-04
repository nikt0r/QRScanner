package pl.polciuta.qrscanner.camera

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.camera.view.PreviewView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.Analyzer
import pl.polciuta.qrscanner.barcode.IBarcode
import pl.polciuta.qrscanner.utils.BitmapUtils

@androidx.camera.core.ExperimentalGetImage
class SharedViewModel(val app: Application) : AndroidViewModel(app) {

    private lateinit var camera: CameraCore
    private lateinit var analyzer: Analyzer
    val barcodeLiveData = MutableLiveData<IBarcode>()
    val messageLiveData = MutableLiveData<String>()
    val cardHeightLiveData = MutableLiveData<Int>()
    private val barcodeData
        get() = barcodeLiveData.value

    override fun onCleared() {
        super.onCleared()
        analyzer.close()
    }

    fun startCamera(previewView: PreviewView) {
        analyzer = Analyzer(barcodeLiveData)
        camera = CameraCore(app, previewView, analyzer)
        camera.startCamera(messageLiveData)

    }

    fun createBitmap(): Bitmap? {
        return takeSnapshot()?.let {
            BitmapUtils.getRoundedCornerBitmap(
                it,
                Color.BLACK,
                app.resources.getDimensionPixelSize(R.dimen.snap_corner),
                app.resources.getDimensionPixelSize(R.dimen.snap_border),
                app.resources.getDimensionPixelSize(R.dimen.snap_height)
            )
        }
    }

    private fun takeSnapshot(): Bitmap? {
        return camera.getPreviewBitmap()?.let { bitmap ->
            barcodeData?.cornerPoints?.let { cornerPoints ->
                BitmapUtils.getBitmapSquareAtLeast(bitmap, cornerPoints, BITMAP_PADDING)
            }
        }
    }

    fun getBarcodePosition(previewWidth: Int, previewHeight: Int) =
        barcodeData?.cornerPoints?.let {
            1f * it.xMin * previewWidth - BITMAP_PADDING to 1f * it.yMin * previewHeight - BITMAP_PADDING
        } ?: 0f to 0f

    fun stopImageAnalysis() {
        analyzer.clearAnalyzer()
    }

    fun restartImageAnalysis() {
        analyzer.setAnalyzer()
    }

    fun postCardHeight(height: Int) {
        cardHeightLiveData.postValue(height)
    }

    fun copyText(text: String) {
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(CLIP_LABEL, text)
        clipboard.setPrimaryClip(clip)
        messageLiveData.postValue(app.getString(R.string.alert_copied))
    }

    companion object {
        private const val LOGTAG = "LOG_SharedViewModel"
        private const val BITMAP_PADDING = 50
        private const val CLIP_LABEL = "text"
    }
}
