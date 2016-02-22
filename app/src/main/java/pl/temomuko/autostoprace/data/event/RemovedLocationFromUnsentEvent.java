package pl.temomuko.autostoprace.data.event;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by Szymon Kozak on 2016-02-13.
 */
public class RemovedLocationFromUnsentEvent {

    LocationRecord mLocationRecord;

    public RemovedLocationFromUnsentEvent(LocationRecord locationRecord) {
        mLocationRecord = locationRecord;
    }

    public LocationRecord getLocationRecord() {
        return mLocationRecord;
    }
}
