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
class BarcodeSMS(
    val phoneNumner: String?,
    val message: String?,
    override val layout: Int = R.layout.card_sms
) : IBarcode {

    override val label = R.string.header_sms

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeSMS, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_phone) to phoneNumner,
            context.getString(R.string.label_message) to message
        )
        return Helpers.createRowList(itemList)
    }

    fun sendSMS(context: Context) {
        phoneNumner?.let {
            Intent(
                Intent.ACTION_VIEW,
                Uri.fromParts("sms", phoneNumner, null)
            ).apply {
                putExtra("sms_body", message)
                context.startActivity(this)
            }
        }
    }
}