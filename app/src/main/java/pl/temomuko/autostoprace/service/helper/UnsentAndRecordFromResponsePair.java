package pl.temomuko.autostoprace.service.helper;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by Szymon Kozak on 2016-03-14.
 */
public class UnsentAndRecordFromResponsePair {

    LocationRecord mUnsentLocationRecord;
    LocationRecord mLocationRecordFromResponse;

    private UnsentAndRecordFromResponsePair(LocationRecord unsentLocationRecord, LocationRecord locationRecordFromResponse) {
        mUnsentLocationRecord = unsentLocationRecord;
        mLocationRecordFromResponse = locationRecordFromResponse;
    }

    public LocationRecord getUnsentLocationRecord() {
        return mUnsentLocationRecord;
    }

    public LocationRecord getLocationRecordFromResponse() {
        return mLocationRecordFromResponse;
    }

    public static UnsentAndRecordFromResponsePair create(LocationRecord unsent, LocationRecord fromResponse) {
        return new UnsentAndRecordFromResponsePair(unsent, fromResponse);
    }
}
