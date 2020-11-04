package pl.polciuta.qrscanner.analyzer

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner

interface IAnalyzer {
    fun initAnalyzer(): ImageAnalysis
    fun clearAnalyzer()
    fun analyze(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy)
}
