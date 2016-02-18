package pl.temomuko.autostoprace;

import com.google.android.gms.location.LocationRequest;

/**
 * Created by szymen on 2016-01-06.
 */
public final class Constants {

    private Constants() {
        throw new AssertionError();
    }

    public final static String API_BASE_URL = "http://api.autostoprace.pl/";
    public final static String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String GOOGLE_PLAY_BASE_URL = "http://play.google.com/store/apps/details?id=";

    public final static String HEADER_ACCEPT_JSON = "application/json";
    public final static String HEADER_CONTENT_TYPE_JSON = "application/json";
    public final static String HEADER_FIELD_TOKEN = "access-token";
    public final static String HEADER_FIELD_CLIENT = "client";
    public final static String HEADER_FIELD_UID = "uid";

    public final static LocationRequest APP_LOCATION_REQUEST = getLocationRequest();
    private static final int LOCATION_UPDATE_INTERVAL_MILLISECONDS = 5000;
    private static final int LOCATION_FASTEST_UPDATE_INTERVAL_MILLISECONDS = LOCATION_UPDATE_INTERVAL_MILLISECONDS / 2;
    private static final int LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    private static LocationRequest getLocationRequest() {
        return new LocationRequest()
                .setFastestInterval(LOCATION_FASTEST_UPDATE_INTERVAL_MILLISECONDS)
                .setInterval(LOCATION_UPDATE_INTERVAL_MILLISECONDS)
                .setPriority(LOCATION_ACCURACY);
    }
}
