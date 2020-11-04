package pl.polciuta.qrscanner.barcode

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.card.InfoRow
import pl.polciuta.qrscanner.utils.Helpers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class BarcodeCalendarEvent(
    val description: String?,
    val start: LocalDateTime?,
    val end: LocalDateTime?,
    val location: String?,
    val organizer: String?,
    val status: String?,
    val summary: String?,
    override val layout: Int = R.layout.card_calendar_event
) : IBarcode {

    override val label = R.string.header_calendar_event

    override lateinit var cornerPoints: RelativeCornerPoints

    private val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeCalendarEvent, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_event_title) to summary,
            context.getString(R.string.label_start_datetime) to start?.format(formatter),
            context.getString(R.string.label_end_datetime) to end?.format(formatter),
            context.getString(R.string.label_location) to location,
            context.getString(R.string.label_organizer) to organizer,
            context.getString(R.string.label_status) to status,
            context.getString(R.string.label_description) to description
        )

        return Helpers.createRowList(itemList)
    }

    @androidx.camera.core.ExperimentalGetImage
    fun saveToCalendar(context: Context) {
        Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI

            start?.let {
                val beginTime: Calendar = Calendar.getInstance()
                beginTime.set(it.year, it.monthValue, it.dayOfMonth, it.hour, it.minute, it.second)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
            }

            end?.let {
                val endTime: Calendar = Calendar.getInstance()
                endTime.set(it.year, it.monthValue, it.dayOfMonth, it.hour, it.minute, it.second)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
            }

            summary?.let { putExtra(CalendarContract.Events.TITLE, it) }
            description?.let { putExtra(CalendarContract.Events.DESCRIPTION, it) }
            location?.let { putExtra(CalendarContract.Events.EVENT_LOCATION, it) }

            context.startActivity(this)
        }
    }

    companion object {
        private const val LOGTAG = "LOG_BarcodeCalendar"
    }
}