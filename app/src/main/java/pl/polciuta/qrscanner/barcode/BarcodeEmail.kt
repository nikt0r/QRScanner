package pl.polciuta.qrscanner.barcode

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.barcode.data.Email
import pl.polciuta.qrscanner.camera.SharedViewModel
import pl.polciuta.qrscanner.card.InfoRow
import pl.polciuta.qrscanner.utils.Helpers

@androidx.camera.core.ExperimentalGetImage
class BarcodeEmail(
    val address: Email?,
    val body: String?,
    val subject: String?,
    override val layout: Int = R.layout.card_email
) :
    IBarcode {

    override val label = R.string.header_email

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeEmail, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_email) to address?.email,
            context.getString(R.string.label_subject) to subject,
            context.getString(R.string.label_body) to body
        )

        return Helpers.createRowList(itemList)
    }

    fun copyEmail(model: SharedViewModel) {
        address?.email?.let { model.copyText("TO: $it\n\nSUBJECT: $subject\n\n$body") }
    }

    fun sendEmail(context: Context) {
        address?.email?.let {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(it))
//                Log.d("LOG_", "sendEmail: $it")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                resolveActivity(context.packageManager)?.let {
                    context.startActivity(this)
                }
            }
        }
    }
}