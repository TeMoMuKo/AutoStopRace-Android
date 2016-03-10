package pl.temomuko.autostoprace.ui.main.adapter;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by Szymon Kozak on 2016-03-10.
 */
public class LocationRecordItem {

    private LocationRecord mLocationRecord;
    private boolean mIsExpanded = false;

    public LocationRecordItem(LocationRecord locationRecord) {
        mLocationRecord = locationRecord;
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

    public LocationRecord getLocationRecord() {
        return mLocationRecord;
    }
}
