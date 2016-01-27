package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by szymen on 2016-01-25.
 */
public class CreateLocationRequest {

    @SerializedName("location") private LocationWrapper mLocationWrapper;

    public CreateLocationRequest(Location loc) {
        mLocationWrapper = new LocationWrapper(loc.getLongitude(), loc.getLatitude(), loc.getMessage());
    }

    private static class LocationWrapper {

        @SerializedName("longitude") private double mLongitude;
        @SerializedName("latitude") private double mLatitude;
        @SerializedName("message") private String mMessage;

        public LocationWrapper(double longitude, double latitude, String message) {
            mLongitude = longitude;
            mLatitude = latitude;
            mMessage = message;
        }
    }
}

