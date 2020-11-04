package pl.polciuta.qrscanner.analyzer

import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import pl.polciuta.qrscanner.barcode.IBarcode
import pl.polciuta.qrscanner.utils.ScopedExecutor
import pl.polciuta.qrscanner.utils.size
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
class Analyzer(private val barcodeLiveData: MutableLiveData<IBarcode>) : IAnalyzer {

    private val cameraExecutor: ScopedExecutor by lazy { ScopedExecutor(Executors.newSingleThreadExecutor()) }
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var barcodeScanner: BarcodeScanner


    override fun initAnalyzer(): ImageAnalysis {
        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetResolution(Size(360, 640))
            .build()

        val options = BarcodeScannerOptions.Builder()
//            .setBarcodeFormats(
//                Barcode.FORMAT_QR_CODE,
//                Barcode.FORMAT_CODE_128
//            )
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        setAnalyzer()

        return imageAnalysis
    }

    fun setAnalyzer() {
        imageAnalysis.setAnalyzer(cameraExecutor, { image ->
            analyze(barcodeScanner, image)
        })
    }

    override fun clearAnalyzer() {
        imageAnalysis.clearAnalyzer()
    }

    override fun analyze(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)

            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.cornerPoints?.let { points ->
//                            Log.d(LOGTAG, "valueType: ${barcode.valueType} | format: ${barcode.format} | points: ${points.asList()}")
                            val mappedBarcode = BarcodeMapper.map(barcode)
                                .apply { cornerPoints = RelativeCornerPoints(points, image.size) }

                            barcodeLiveData.postValue(mappedBarcode)
                        }
                    }
                }
                .addOnFailureListener {
                    // TODO: handle failure
//                    Log.e(LOGTAG, "${it.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    fun close() {
        cameraExecutor.shutdown()
    }

    companion object {
        private const val LOGTAG = "LOG_Analyzer"
    }
}
