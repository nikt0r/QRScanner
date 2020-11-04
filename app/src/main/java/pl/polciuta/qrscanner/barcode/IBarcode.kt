package pl.polciuta.qrscanner.barcode

import android.content.Context
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.card.InfoRow

interface IBarcode {
    val label: Int
    val layout: Int
    var cornerPoints: RelativeCornerPoints
    fun bindData(binding: ViewDataBinding)
    fun createDisplayList(context: Context): List<InfoRow>
    fun getLabel(context: Context) = context.resources.getString(label)
}