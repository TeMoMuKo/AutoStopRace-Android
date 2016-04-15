package pl.temomuko.autostoprace.ui.teamslocations.adapter;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.util.DateUtil;

/**
 * Created by Rafał Naniewicz on 02.04.2016.
 */
public class LocationRecordClusterItem implements ClusterItem {

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
}
