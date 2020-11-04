package pl.polciuta.qrscanner.barcode

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.camera.SharedViewModel
import pl.polciuta.qrscanner.card.InfoRow
import pl.polciuta.qrscanner.utils.Helpers

@androidx.camera.core.ExperimentalGetImage
class BarcodeProduct(
    val code: String?, override val layout: Int = R.layout.card_product
) : IBarcode {

    override val label = R.string.header_product

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeProduct, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_product) to code
        )

        return Helpers.createRowList(itemList)
    }

    fun copyCode(model: SharedViewModel) {
        code?.let { model.copyText(it) }
    }

    fun lookupCode(context: Context) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.barcodelookup.com/$code"))
        context.startActivity(webIntent)
    }
}