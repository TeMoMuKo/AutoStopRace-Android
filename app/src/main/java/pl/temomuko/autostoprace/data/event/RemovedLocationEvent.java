package pl.temomuko.autostoprace.data.event;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by szymen on 2016-02-13.
 */
public class RemovedLocationEvent {

    LocationRecord mLocationRecord;

    public RemovedLocationEvent(LocationRecord locationRecord) {
        mLocationRecord = locationRecord;
    }

    public LocationRecord getLocationRecord() {
        return mLocationRecord;
    }
}
