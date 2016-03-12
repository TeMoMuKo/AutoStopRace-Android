package pl.temomuko.autostoprace;

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

    public static final String GOOGLE_PLAY_BASE_URL = "http://play.google.com/store/apps/details?id=";
    public final static String HEADER_FIELD_TOKEN = "access-token";
    public final static String HEADER_FIELD_CLIENT = "client";
    public final static String HEADER_FIELD_UID = "uid";
    public final static String HEADER_VALUE_APPLICATION_JSON = "application/json";

    public static final float MAX_LOCATION_ACCURACY = 60.0f;
    public static final long SPLASH_DURATION = 700;
}
