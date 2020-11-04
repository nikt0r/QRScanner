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
data class BarcodeISBN(
    val code: String?, override val layout: Int = R.layout.card_isbn
) : IBarcode {

    override val label = R.string.header_isbn

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeISBN, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_isbn) to code
        )

        return Helpers.createRowList(itemList)
    }

    fun lookupISBN(context: Context) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.barcodelookup.com/$code"))
        context.startActivity(webIntent)
    }

    fun copyISBN(model: SharedViewModel) {
        code?.let { model.copyText(it) }
    }
}