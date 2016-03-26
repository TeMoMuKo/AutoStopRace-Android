package pl.temomuko.autostoprace.data.event;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by Rafał Naniewicz on 24.03.2016.
 */
public class SuccessfullyAddedToUnsentTableEvent {

    private LocationRecord mUnsentLocationRecord;

    public SuccessfullyAddedToUnsentTableEvent(LocationRecord unsentLocationRecord) {

        mUnsentLocationRecord = unsentLocationRecord;
    }

    public LocationRecord getUnsentLocationRecord() {
        return mUnsentLocationRecord;
    }
}
