package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.util.DateUtil;

/**
 * Created by Rafał Naniewicz on 02.04.2016.
 */
public class LocationRecordClusterItem implements ClusterItem, Parcelable {

    private LatLng mLatLng;
    private String mMessage;
    private Date mReceiptDate;

    public LocationRecordClusterItem(double latitude, double longitude, String message, @Nullable Date receiptDate) {
        mLatLng = new LatLng(latitude, longitude);
        mMessage = message;
        mReceiptDate = receiptDate;
    }

    public LocationRecordClusterItem(LocationRecord locationRecord) {
        mLatLng = new LatLng(locationRecord.getLatitude(), locationRecord.getLongitude());
        mMessage = locationRecord.getMessage();
        if (mMessage.isEmpty()) {
            mMessage = null;
        }
        mReceiptDate = locationRecord.getServerReceiptDate();
    }

    public String getMessage() {
        return mMessage;
    }

    public String getReceiptDateString() {
        return mReceiptDate == null ? null : DateUtil.getFullDateMapString(mReceiptDate);
    }

    public Date getReceiptDate() {
        return mReceiptDate;
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }

    protected LocationRecordClusterItem(Parcel in) {
        mLatLng = (LatLng) in.readValue(LatLng.class.getClassLoader());
        mMessage = in.readString();
        long tmpMReceiptDate = in.readLong();
        mReceiptDate = tmpMReceiptDate != -1 ? new Date(tmpMReceiptDate) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mLatLng);
        dest.writeString(mMessage);
        dest.writeLong(mReceiptDate != null ? mReceiptDate.getTime() : -1L);
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