package pl.temomuko.autostoprace.service.helper;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import retrofit2.Response;

/**
 * Created by Szymon Kozak on 2016-03-14.
 */
public final class UnsentLocationRecordAndServerResponsePair {

    private LocationRecord mUnsentLocationRecord;
    private Response<LocationRecord> mLocationRecordResponse;

    private UnsentLocationRecordAndServerResponsePair(LocationRecord unsentLocationRecord,
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

    public static UnsentLocationRecordAndServerResponsePair create(LocationRecord locationRecord,
                                                                   Response<LocationRecord> locationRecordResponse) {
        return new UnsentLocationRecordAndServerResponsePair(locationRecord, locationRecordResponse);
    }
}