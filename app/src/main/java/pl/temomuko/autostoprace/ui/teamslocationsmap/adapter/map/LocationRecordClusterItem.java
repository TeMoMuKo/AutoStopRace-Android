package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.util.DateUtil;

public class LocationRecordClusterItem implements ClusterItem, Parcelable, Comparable<LocationRecordClusterItem> {

    private final LatLng mLatLng;
    @Nullable
    private final String mTitle;
    private final Date mReceiptDate;
    private final String mReceiptDateString;

    @Nullable
    private final String mImageUriString;

    public LocationRecordClusterItem(LocationRecord locationRecord) {
        mLatLng = new LatLng(locationRecord.getLatitude(), locationRecord.getLongitude());
        mTitle = locationRecord.getMessage();
        mReceiptDate = locationRecord.getServerReceiptDate();
        mReceiptDateString = mReceiptDate == null ? null : DateUtil.getFullDateMapString(mReceiptDate);
        mImageUriString = locationRecord.getImageLocationString();
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }

    @Nullable
    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mReceiptDateString;
    }

    @Nullable
    public Uri getImageUri() {
        return mImageUriString == null ? null : Uri.parse(mImageUriString);
    }

    @Override
    public int compareTo(@NonNull LocationRecordClusterItem another) {
        return getDateCompareResult(another);
    }

    public Date getReceiptDate() {
        return mReceiptDate;
    }

    private int getDateCompareResult(@NonNull LocationRecordClusterItem another) {
        int dateCompareResult;
        if (mReceiptDate == null && another.mReceiptDate == null) {
            dateCompareResult = 0;
        } else if (mReceiptDate == null) {
            dateCompareResult = -1;
        } else if (another.mReceiptDate == null) {
            dateCompareResult = 1;
        } else {
            dateCompareResult = another.mReceiptDate.compareTo(mReceiptDate);
        }
        return dateCompareResult;
    }

    /*Parcel*/

    protected LocationRecordClusterItem(Parcel in) {
        mLatLng = (LatLng) in.readValue(LatLng.class.getClassLoader());
        mTitle = in.readString();
        long tmpMReceiptDate = in.readLong();
        mReceiptDate = tmpMReceiptDate != -1 ? new Date(tmpMReceiptDate) : null;
        mReceiptDateString = in.readString();
        mImageUriString = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mLatLng);
        dest.writeString(mTitle);
        dest.writeLong(mReceiptDate != null ? mReceiptDate.getTime() : -1L);
        dest.writeString(mReceiptDateString);
        dest.writeString(mImageUriString);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LocationRecordClusterItem> CREATOR = new Parcelable.Creator<LocationRecordClusterItem>() {
        @Override
        public LocationRecordClusterItem createFromParcel(Parcel in) {
            return new LocationRecordClusterItem(in);
        }

        @Override
        public LocationRecordClusterItem[] newArray(int size) {
            return new LocationRecordClusterItem[size];
        }
    };
}