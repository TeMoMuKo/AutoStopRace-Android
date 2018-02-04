package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall

import pl.temomuko.autostoprace.data.model.LocationRecord
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
        //todo
        return date.toString()
    }
}