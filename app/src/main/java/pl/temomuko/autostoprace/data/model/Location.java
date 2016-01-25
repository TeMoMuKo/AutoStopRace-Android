package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by szymen on 2016-01-22.
 */
public class Location {

    @SerializedName("location_id") private int mLocationId;
    @SerializedName("team_id") private int mTeamId;
    @SerializedName("latitude") private double mLatitude;
    @SerializedName("longitude") private double mLongitude;
    @SerializedName("message") private String mMessage;
    @SerializedName("created_at") private Date mCreatedDate;
    @SerializedName("updated_at") private Date mUpdatedDate;

    private boolean mIsOnServer = true;

    public Location(double latitude, double longitude, String message) {
        mLatitude = latitude;
        mLongitude = longitude;
        mMessage = message;
    }

    public int getLocationId() {
        return mLocationId;
    }

    public int getTeamId() {
        return mTeamId;
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

    public Date getCreatedDate() {
        return mCreatedDate;
    }

    public Date getUpdatedDate() {
        return mUpdatedDate;
    }

    public boolean isOnServer() {
        return mIsOnServer;
    }

    public void setIsSent(boolean isSent) {
        mIsOnServer = isSent;
    }

    public String toString() {
        return "(" + getLatitude() + ", " + getLongitude() + ")";
    }
}
