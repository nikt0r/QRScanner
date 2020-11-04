package pl.polciuta.qrscanner.analyzer

import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.Barcode.WiFi.*
import pl.polciuta.qrscanner.barcode.*
import pl.polciuta.qrscanner.barcode.data.*
import java.time.DateTimeException
import java.time.LocalDateTime

@androidx.camera.core.ExperimentalGetImage
object BarcodeMapper {

    fun map(barcode: Barcode): IBarcode {

        return when (barcode.valueType) {
            Barcode.TYPE_WIFI -> {
                BarcodeWifi(
                    barcode.wifi?.ssid,
                    barcode.wifi?.password,
                    barcode.wifi?.encryptionType?.let {
                        when (it) {
                            TYPE_OPEN -> WifiEncryptionType.OPEN
                            TYPE_WEP -> WifiEncryptionType.WEP
                            TYPE_WPA -> WifiEncryptionType.WPA
                            else -> WifiEncryptionType.UNKNOWN
                        }
                    }
                )
            }

            Barcode.TYPE_EMAIL -> {
                BarcodeEmail(
                    Email(
                        barcode.email?.address,
                        when (barcode.email?.type) {
                            Barcode.Email.TYPE_HOME -> Email.Type.HOME
                            Barcode.Email.TYPE_WORK -> Email.Type.WORK
                            else -> Email.Type.UNKNOWN
                        }
                    ),
                    barcode.email?.body,
                    barcode.email?.subject
                )
            }

            Barcode.TYPE_TEXT -> {
                // TODO: uncomment BarcodeText, remove BarcodeUnknown
                BarcodeText(
                    barcode.rawBytes?.decodeToString()
                )
//                BarcodeUnknown(barcode.rawValue)
            }

            Barcode.TYPE_URL -> {
                BarcodeUrl(
                    barcode.url?.title,
                    barcode.url?.url
                )
            }

            Barcode.TYPE_CALENDAR_EVENT -> {
                BarcodeCalendarEvent(
                    barcode.calendarEvent?.description,
                    barcode.calendarEvent?.start?.let { convertToLocalDateTime(it) },
                    barcode.calendarEvent?.end?.let { convertToLocalDateTime(it) },
                    barcode.calendarEvent?.location,
                    barcode.calendarEvent?.organizer,
                    barcode.calendarEvent?.status,
                    barcode.calendarEvent?.summary
                )
            }

            Barcode.TYPE_CONTACT_INFO -> {
                BarcodeContact(
                    barcode.contactInfo?.addresses?.map {
                        StreetAddress(
                            it.addressLines.toList(),
                            when (it.type) {
                                Barcode.Address.TYPE_HOME -> StreetAddress.Type.HOME
                                Barcode.Address.TYPE_WORK -> StreetAddress.Type.WORK
                                else -> StreetAddress.Type.UNKNOWN
                            }
                        )
                    },
                    barcode.contactInfo?.emails?.map {
                        Email(
                            it.address,
                            when (it.type) {
                                Barcode.Email.TYPE_HOME -> Email.Type.HOME
                                Barcode.Email.TYPE_WORK -> Email.Type.WORK
                                else -> Email.Type.UNKNOWN
                            }
                        )
                    },
                    Person(
                        barcode.contactInfo?.name?.first,
                        barcode.contactInfo?.name?.last,
                        barcode.contactInfo?.name?.middle,
                        barcode.contactInfo?.name?.prefix,
                        barcode.contactInfo?.name?.suffix,
                        barcode.contactInfo?.name?.pronunciation,
                        barcode.contactInfo?.name?.formattedName
                    ),
                    barcode.contactInfo?.organization,
                    barcode.contactInfo?.phones?.map {
                        Phone(
                            it.number,
                            when (it.type) {
                                Barcode.Phone.TYPE_FAX -> Phone.Type.FAX
                                Barcode.Phone.TYPE_HOME -> Phone.Type.HOME
                                Barcode.Phone.TYPE_MOBILE -> Phone.Type.MOBILE
                                Barcode.Phone.TYPE_WORK -> Phone.Type.WORK
                                else -> Phone.Type.UNKNOWN
                            }
                        )
                    },
                    barcode.contactInfo?.title,
                    barcode.contactInfo?.urls
                )
            }

//            Barcode.TYPE_DRIVER_LICENSE -> {
//                // TODO:
//            }

            Barcode.TYPE_GEO -> {
                BarcodeGeo(
                    barcode.geoPoint?.lat,
                    barcode.geoPoint?.lng
                )
            }

            Barcode.TYPE_ISBN -> {
                BarcodeISBN(
                    barcode.rawBytes?.decodeToString()
                )
            }

            Barcode.TYPE_PHONE -> {
                BarcodePhone(
                    Phone(
                        barcode.phone?.number,
                        when (barcode.phone?.type) {
                            Barcode.Phone.TYPE_FAX -> Phone.Type.FAX
                            Barcode.Phone.TYPE_HOME -> Phone.Type.HOME
                            Barcode.Phone.TYPE_MOBILE -> Phone.Type.MOBILE
                            Barcode.Phone.TYPE_WORK -> Phone.Type.WORK
                            else -> Phone.Type.UNKNOWN
                        }
                    )
                )
            }

            Barcode.TYPE_PRODUCT -> {
                BarcodeProduct(
                    barcode.rawBytes?.decodeToString()
                )
            }

            Barcode.TYPE_SMS -> {
                BarcodeSMS(
                    barcode.sms?.phoneNumber,
                    barcode.sms?.message
                )
            }

            Barcode.TYPE_UNKNOWN -> {
                BarcodeUnknown(barcode.rawValue)
            }

            else -> BarcodeUnknown(barcode.rawValue)
        }

    }

    private fun convertToLocalDateTime(barcodeDateTime: Barcode.CalendarDateTime): LocalDateTime? {
        return try {
            LocalDateTime.of(
                barcodeDateTime.year,
                barcodeDateTime.month,
                barcodeDateTime.day,
                barcodeDateTime.hours,
                barcodeDateTime.minutes,
                barcodeDateTime.seconds
            )
        } catch (e: DateTimeException) {
            null
        }
    }
}
