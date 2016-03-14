package pl.temomuko.autostoprace.service.helper;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import retrofit2.Response;

/**
 * Created by Szymon Kozak on 2016-03-14.
 */
public final class UnsentAndServerResponsePair {

    private LocationRecord mUnsentLocationRecord;
    private Response<LocationRecord> mLocationRecordResponse;

    private UnsentAndServerResponsePair(LocationRecord unsentLocationRecord,
                                       Response<LocationRecord> locationRecordResponse) {
        mLocationRecordResponse = locationRecordResponse;
        mUnsentLocationRecord = unsentLocationRecord;
    }

    public LocationRecord getUnsentLocationRecord() {
        return mUnsentLocationRecord;
    }

    public Response<LocationRecord> getLocationRecordResponse() {
        return mLocationRecordResponse;
    }

    public static UnsentAndServerResponsePair create(LocationRecord locationRecord,
                                                     Response<LocationRecord> locationRecordResponse) {
        return new UnsentAndServerResponsePair(locationRecord, locationRecordResponse);
    }
}