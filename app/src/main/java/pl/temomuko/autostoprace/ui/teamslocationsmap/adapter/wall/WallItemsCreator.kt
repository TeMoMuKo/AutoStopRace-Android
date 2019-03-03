package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall

import android.text.format.DateUtils
import android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE
import pl.temomuko.autostoprace.domain.model.LocationRecord
import pl.temomuko.autostoprace.util.LocationInfoProvider
import java.util.*
import javax.inject.Inject

class WallItemsCreator @Inject constructor(
    private val locationInfoProvider: LocationInfoProvider
) {

    fun createFromLocationRecords(locationRecords: List<LocationRecord>): List<WallItem> {
        return locationRecords.map { createWallItem(it) }
    }

    private fun createWallItem(locationRecord: LocationRecord): WallItem {
        return WallItem(
            message = locationRecord.message,
            locationInfo = locationInfoProvider.getLocationInfo(locationRecord),
            timeInfo = getTimeInfo(locationRecord.serverReceiptDate),
            imageUrl = locationRecord.imageLocationString
        )
    }

    private fun getTimeInfo(date: Date): String {
        return DateUtils.getRelativeTimeSpanString(
            date.time,
            Calendar.getInstance().timeInMillis,
            0,
            FORMAT_ABBREV_RELATIVE
        ).toString()
    }
}
