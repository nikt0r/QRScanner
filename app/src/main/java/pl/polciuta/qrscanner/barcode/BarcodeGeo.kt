package pl.polciuta.qrscanner.barcode

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.card.InfoRow
import pl.polciuta.qrscanner.utils.Helpers

data class BarcodeGeo(
    val latitude: Double?, val longitude: Double?, override val layout: Int = R.layout.card_geo
) : IBarcode {

    override val label = R.string.header_geo

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeGeo, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_latitude) to latitude.toString(),
            context.getString(R.string.label_longitude) to longitude.toString()
        )

        return Helpers.createRowList(itemList)
    }

    @androidx.camera.core.ExperimentalGetImage
    fun openMap(context: Context) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=$latitude,$longitude"))
        context.startActivity(webIntent)
    }
}