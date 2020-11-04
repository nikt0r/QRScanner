package pl.polciuta.qrscanner.barcode

import android.content.Context
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.camera.SharedViewModel
import pl.polciuta.qrscanner.card.InfoRow
import pl.polciuta.qrscanner.utils.Helpers

@androidx.camera.core.ExperimentalGetImage
class BarcodeUnknown(val rawValue: String?,  override val layout: Int = R.layout.card_unknown) : IBarcode {

    override val label = R.string.header_unknown

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeUnknown, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_raw_value) to rawValue
        )
        return Helpers.createRowList(itemList)
    }

    fun copyRawValue(model: SharedViewModel) {
        rawValue?.let { model.copyText(it) }
    }

}
