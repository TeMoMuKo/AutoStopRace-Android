package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Szymon Kozak on 2016-01-25.
 */
public class CreateLocationRecordRequest {

    @SerializedName("location") private LocationWrapper mLocationWrapper;

    public CreateLocationRecordRequest(LocationRecord loc) {
        mLocationWrapper = new LocationWrapper(loc.getLatitude(), loc.getLongitude(), loc.getMessage());
    }

    private static class LocationWrapper {

        @SerializedName("latitude") private double mLatitude;
        @SerializedName("longitude") private double mLongitude;
        @SerializedName("message") private String mMessage;

        public LocationWrapper(double latitude, double longitude, String message) {
            mLongitude = longitude;
            mLatitude = latitude;
            mMessage = message;
        }
    }
}

