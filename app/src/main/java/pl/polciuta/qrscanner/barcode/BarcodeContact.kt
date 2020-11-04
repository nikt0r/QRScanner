package pl.polciuta.qrscanner.barcode

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.barcode.data.Email
import pl.polciuta.qrscanner.barcode.data.Person
import pl.polciuta.qrscanner.barcode.data.Phone
import pl.polciuta.qrscanner.barcode.data.StreetAddress
import pl.polciuta.qrscanner.card.InfoRow
import pl.polciuta.qrscanner.utils.Helpers
import java.util.*

@androidx.camera.core.ExperimentalGetImage
data class BarcodeContact(
    val addresses: List<StreetAddress>?,
    val emails: List<Email>?,
    val name: Person?,
    val organization: String?,
    val phones: List<Phone>?,
    val title: String?,
    val urls: List<String>?,
    override val layout: Int = R.layout.card_contact
) : IBarcode {

    override val label = R.string.header_contact

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeContact, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
//        Log.d(LOGTAG, "saveContact: ${this}")

        mutableListOf<Pair<String, String?>>().apply {
            add(context.getString(R.string.label_name) to name?.formattedName)
            add(context.getString(R.string.label_contact_title) to title)
            add(context.getString(R.string.label_emails) to
                    emails?.joinToString("\n") { email ->
                        val n = email.email ?: ""
                        val t = email.type.toString().let { if (it.isNotEmpty()) " (${it.toLowerCase(Locale.ROOT)})" else "" }
                        "$n$t"
                    }
            )
            add(context.getString(R.string.label_phones) to
                    phones?.joinToString("\n") { phone ->
                        val n = phone.number ?: ""
                        val t = phone.type.toString().let { if (it.isNotEmpty()) " (${it.toLowerCase(Locale.ROOT)})" else "" }
                        "$n$t"
                    }

            )
            add(
                context.getString(R.string.label_address) to

                        addresses?.joinToString("\n--\n") {
                            it.addressLines.joinToString("\n")
                        }
            )
            add(context.getString(R.string.label_organization) to organization)
            add(context.getString(R.string.label_urls) to urls?.joinToString("\n"))
            return Helpers.createRowList(this)
        }
    }

    fun saveContact(context: Context) {

        Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, name?.formattedName)
            putExtra(ContactsContract.Intents.Insert.PHONETIC_NAME, name?.pronunciation)
            putExtra(ContactsContract.Intents.Insert.COMPANY, organization)
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, title)

            val data = ArrayList<ContentValues>()
            addPhones(data)
            addEmails(data)
            addAddresses(data)
            addUrls(data)
            putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data)
            context.startActivity(this)
        }

    }

    private fun addUrls(data: ArrayList<ContentValues>) {
        urls?.forEach {
            val row = ContentValues()
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
            row.put(ContactsContract.CommonDataKinds.Website.URL, it)
            data.add(row)
        }
    }

    private fun addAddresses(data: ArrayList<ContentValues>) {
        addresses?.forEach {
            val row = ContentValues()
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
            row.put(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, it.addressLines.joinToString("\n"))
            row.put(
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                when (it.type) {
                    StreetAddress.Type.HOME -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME
                    StreetAddress.Type.WORK -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK
                    else -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER
                }
            )
            data.add(row)
        }
    }

    private fun addEmails(data: ArrayList<ContentValues>) {
        emails?.forEach {
            val row = ContentValues()
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            row.put(ContactsContract.CommonDataKinds.Email.ADDRESS, it.email)
            row.put(
                ContactsContract.CommonDataKinds.Email.TYPE,
                when (it.type) {
                    Email.Type.HOME -> ContactsContract.CommonDataKinds.Email.TYPE_HOME
                    Email.Type.WORK -> ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    else -> ContactsContract.CommonDataKinds.Email.TYPE_OTHER
                }
            )
            data.add(row)
        }
    }

    private fun addPhones(data: ArrayList<ContentValues>) {
        phones?.forEach {
            val row = ContentValues()
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, it.number)
            row.put(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                when (it.type) {
                    Phone.Type.HOME -> ContactsContract.CommonDataKinds.Phone.TYPE_HOME
                    Phone.Type.WORK -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK
                    Phone.Type.MOBILE -> ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    Phone.Type.FAX -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX
                    else -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
                }
            )
            data.add(row)
        }
    }

    companion object {
        private const val LOGTAG = "LOG_BarcodeContact"
    }
}