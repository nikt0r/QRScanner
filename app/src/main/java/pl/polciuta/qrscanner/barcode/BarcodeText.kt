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
class BarcodeText(val text: String?, override val layout: Int = R.layout.card_text) : IBarcode {

    override val label = R.string.header_text

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeText, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_text_content) to text
        )
        return Helpers.createRowList(itemList)
    }

    fun copyText(model: SharedViewModel) {
        text?.let { model.copyText(it) }
    }
}