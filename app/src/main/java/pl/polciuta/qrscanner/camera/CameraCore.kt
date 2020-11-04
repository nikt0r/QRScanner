package pl.polciuta.qrscanner.camera

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewTreeLifecycleOwner
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.Analyzer
import pl.polciuta.qrscanner.analyzer.IAnalyzer
import java.util.concurrent.Executor

@androidx.camera.core.ExperimentalGetImage
class CameraCore(
    private val context: Context,
    private val cameraPreview: PreviewView,
    private val analyzer: Analyzer
) :
    IAnalyzer by analyzer {

    private lateinit var cameraSelector: CameraSelector
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var camera: Camera
    private lateinit var imageAnalysis: ImageAnalysis
    private val mainExecutor: Executor by lazy { ContextCompat.getMainExecutor(context) }

    fun startCamera(messageLiveData: MutableLiveData<String>) {
        val owner = ViewTreeLifecycleOwner.get(cameraPreview) ?: return

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetResolution(Size(720, 1280))
                .build()

            cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            imageAnalysis = initAnalyzer()

            try {
                preview.setSurfaceProvider(cameraPreview.surfaceProvider)

                camera = cameraProvider.bindToLifecycle(
                    owner, cameraSelector, preview, imageAnalysis
                )
            } catch (e: IllegalStateException) {
                // If the use case has already been bound to another
                // lifecycle or method is not called on main thread
//                Log.d(LOGTAG, e.javaClass.simpleName)
                messageLiveData.postValue(context.getString(R.string.msg_usecase_already_bound))
            } catch (e: IllegalArgumentException) {
                // If the provided camera selector is unable to resolve
                // a camera to be used for the given use cases
//                Log.d(LOGTAG, e.javaClass.simpleName)
                messageLiveData.postValue(context.getString(R.string.msg_cant_resolve_camera))
            }

        }, mainExecutor)
    }

    fun unbind() {
        if (::cameraProvider.isInitialized)
            cameraProvider.unbindAll()
    }

    fun getPreviewBitmap() = cameraPreview.bitmap


    companion object {
        private const val LOGTAG = "LOG_CameraCore"
    }
}
