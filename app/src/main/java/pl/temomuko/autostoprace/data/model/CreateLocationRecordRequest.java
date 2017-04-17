package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Szymon Kozak on 2016-01-25.
 */
public class CreateLocationRecordRequest {

    @SerializedName("location") private LocationWrapper mLocationWrapper;

    public CreateLocationRecordRequest(double latitude, double longitude, String message, String base64Image) {
        mLocationWrapper = new LocationWrapper(latitude, longitude, message, base64Image);
    }

    private static class LocationWrapper {

        @SerializedName("latitude") private double mLatitude;
        @SerializedName("longitude") private double mLongitude;
        @SerializedName("message") private String mMessage;
        @SerializedName("image") private String mBase64Image;

        public LocationWrapper(double latitude, double longitude, String message, String base64Image) {
            mLongitude = longitude;
            mLatitude = latitude;
            mMessage = message;
            mBase64Image = base64Image;
        }
    }
}

