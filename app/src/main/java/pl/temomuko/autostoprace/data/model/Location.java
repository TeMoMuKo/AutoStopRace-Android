package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by szymen on 2016-01-22.
 */
public class Location {

    @SerializedName("location_id") private int mLocationId;
    @SerializedName("latitude") private double mLatitude;
    @SerializedName("longitude") private double mLongitude;
    @SerializedName("message") private String mMessage;
    @SerializedName("created_at") private Date mServerReceiptDate;

    @SerializedName("id") private int mTemporaryId;

    public Location() {
    }

    public Location(double latitude, double longitude, String message) {
        mLatitude = latitude;
        mLongitude = longitude;
        mMessage = message;
    }

    public int getLocationId() {
        return mLocationId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getMessage() {
        return mMessage;
    }

    public Date getServerReceiptDate() {
        return mServerReceiptDate;
    }

    public void setLocationId(int locationId) {
        mLocationId = locationId;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setServerReceiptDate(Date serverReceiptDate) {
        mServerReceiptDate = serverReceiptDate;
    }

    public int getTemporaryId() {
        return mTemporaryId;
    }

    public String toString() {
        return "(" + getLatitude() + ", " + getLongitude() + ", " + getMessage() + ")\n";
    }
}
