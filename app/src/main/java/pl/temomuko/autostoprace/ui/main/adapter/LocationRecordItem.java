package pl.temomuko.autostoprace.ui.main.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Comparator;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by Szymon Kozak on 2016-03-10.
 */
public class LocationRecordItem implements Parcelable, Comparable<LocationRecordItem> {

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

    protected LocationRecordItem(Parcel in) {
        mLocationRecord = (LocationRecord) in.readValue(LocationRecord.class.getClassLoader());
        mIsExpanded = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mLocationRecord);
        dest.writeByte((byte) (mIsExpanded ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LocationRecordItem> CREATOR = new Parcelable.Creator<LocationRecordItem>() {
        @Override
        public LocationRecordItem createFromParcel(Parcel in) {
            return new LocationRecordItem(in);
        }

        @Override
        public LocationRecordItem[] newArray(int size) {
            return new LocationRecordItem[size];
        }
    };

    @Override
    public int compareTo(@NonNull LocationRecordItem anotherLocationRecordItem) {
        return this.getLocationRecord().compareTo(anotherLocationRecordItem.getLocationRecord());
    }
}
