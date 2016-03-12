package pl.temomuko.autostoprace.data.event;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by Szymon Kozak on 2016-02-13.
 */
public class SuccessfullySentLocationToServerEvent {

    private LocationRecord mDeletedUnsentLocationRecord;
    private LocationRecord mReceivedLocationRecord;

    public SuccessfullySentLocationToServerEvent(LocationRecord deletedUnsentLocationRecord,
                                                 LocationRecord receivedLocationRecord) {
        mDeletedUnsentLocationRecord = deletedUnsentLocationRecord;
        mReceivedLocationRecord = receivedLocationRecord;
    }

    public LocationRecord getDeletedUnsentLocationRecord() {
        return mDeletedUnsentLocationRecord;
    }

    public LocationRecord getReceivedLocationRecord() {
        return mReceivedLocationRecord;
    }
}
