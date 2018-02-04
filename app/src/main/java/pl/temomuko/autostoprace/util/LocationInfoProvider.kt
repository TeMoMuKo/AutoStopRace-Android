package pl.temomuko.autostoprace.util

import pl.temomuko.autostoprace.data.model.LocationRecord
import javax.inject.Inject

class LocationInfoProvider @Inject constructor() {

    fun getLocationInfo(locationRecord: LocationRecord): String {
        return if (locationRecord.address.isNullOrBlank()) {
            CoordsUtil.getDmsTextFromDecimalDegrees(locationRecord.latitude, locationRecord.longitude)
        } else {
            locationRecord.address!!
        }
    }
}