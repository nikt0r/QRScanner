package pl.polciuta.qrscanner.barcode

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.camera.SharedViewModel
import pl.polciuta.qrscanner.card.InfoRow
import pl.polciuta.qrscanner.utils.Helpers

@androidx.camera.core.ExperimentalGetImage
class BarcodeUrl(val title: String?, val url: String?, override val layout: Int = R.layout.card_url) : IBarcode {

    override val label = R.string.header_url

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeUrl, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_url_title) to title,
            context.getString(R.string.label_url) to url
        )

        return Helpers.createRowList(itemList)
    }

    fun copyUrl(model: SharedViewModel) {
        url?.let { model.copyText(it) }
    }

    fun open(context: Context) {
//        Log.d(LOGTAG, "setListeners: CLICK $url")
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(webIntent)
    }

    companion object {
        private const val LOGTAG = "LOG_BarcodeUrl"
    }

}