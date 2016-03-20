package pl.temomuko.autostoprace;

import com.google.android.gms.location.LocationRequest;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
public final class Constants {

    private Constants() {
        throw new AssertionError();
    }

    public static final String DEFAULT_LOCALE = "pl";

    public final static String API_BASE_URL = "https://api.autostoprace.pl/";
    public final static String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public final static String APP_DATE_FORMAT = "d.LL";
    public final static String APP_TIME_FORMAT = "HH:mm";
    public final static String SERVER_TIMEZONE = "UTC";
    public static final String GOOGLE_PLAY_BASE_URL = "http://play.google.com/store/apps/details?id=";

    public final static String HEADER_FIELD_TOKEN = "access-token";
    public final static String HEADER_FIELD_CLIENT = "client";
    public final static String HEADER_FIELD_UID = "uid";
    public static final String API_RESET_PASS_REDIRECT_URL = "http://autostoprace.pl";
    public final static String HEADER_VALUE_APPLICATION_JSON = "application/json";

    public static final int HTTP_CONNECT_TIMEOUT = 10;
    public static final int HTTP_READ_TIMEOUT = 15;
    public static final int HTTP_WRITE_TIMEOUT = 15;

    public static final float MAX_LOCATION_ACCURACY = 150.0f;
    public static final long SPLASH_DURATION = 1800;

    public static final int APP_LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public static final int LOCATION_UPDATE_INTERVAL_MILLISECONDS = 7000;
    public static final int LOCATION_FASTEST_UPDATE_INTERVAL_MILLISECONDS = LOCATION_UPDATE_INTERVAL_MILLISECONDS / 2;
    public static final int GEO_CODING_TIMEOUT_MILLISECONDS = LOCATION_FASTEST_UPDATE_INTERVAL_MILLISECONDS;
}
