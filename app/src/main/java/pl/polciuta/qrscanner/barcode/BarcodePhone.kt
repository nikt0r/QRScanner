package pl.polciuta.qrscanner.barcode

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.barcode.data.Phone
import pl.polciuta.qrscanner.camera.SharedViewModel
import pl.polciuta.qrscanner.card.InfoRow
import pl.polciuta.qrscanner.utils.Helpers
import java.util.*

@androidx.camera.core.ExperimentalGetImage
data class BarcodePhone(
    val phone: Phone?,
    override val layout: Int = R.layout.card_phone
) : IBarcode {

    override val label = R.string.header_phone

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodePhone, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {

        val n = phone?.number ?: ""
        val t = phone?.type.toString().let { if (it.isNotEmpty()) " (${it.toLowerCase(Locale.ROOT)})" else "" }

        val itemList = listOf(
            context.getString(R.string.label_phone) to "$n$t"
        )

        return Helpers.createRowList(itemList)
    }

    fun callNumber(context: Context) {
        phone?.number?.let {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$it")
            context.startActivity(intent)
        }
    }

    fun copyNumber(model: SharedViewModel) {
        phone?.number?.let { model.copyText(it) }
    }

}