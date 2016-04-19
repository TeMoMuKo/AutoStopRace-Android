package pl.temomuko.autostoprace;

import com.google.android.gms.location.LocationRequest;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
public final class Constants {

    private Constants() {
        throw new AssertionError();
    }

    /* General / API */
    public static final String DEFAULT_LOCALE = "pl";

    public final static String API_BASE_URL = "https://api.autostoprace.pl/";

    public final static String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public final static String APP_DATE_FORMAT = "d.LL";
    public final static String APP_TIME_FORMAT = "HH:mm";
    public final static String DATABASE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public final static String MAP_FULL_DATE_FORMAT = "HH:MM d.LL";
    public final static String SERVER_TIMEZONE = "UTC";
    public static final String GOOGLE_PLAY_BASE_URL = "http://play.google.com/store/apps/details?id=";

    public final static String HEADER_FIELD_TOKEN = "access-token";
    public final static String HEADER_FIELD_CLIENT = "client";
    public final static String HEADER_FIELD_UID = "uid";
    public static final String LICENSES_ASSET_URI = "file:///android_asset/licenses.html";
    public static final String API_RESET_PASS_REDIRECT_URL = "http://autostoprace.pl";
    public static final String URL_MAP = "https://mapa.autostoprace.pl/";
    public static final String URL_MAP_TEAM_NUMBER_PARAM = "team";
    public static final String URL_MAP_TEAM_NUMBER_QUERY_KEY = "?" + URL_MAP_TEAM_NUMBER_PARAM + "=";

    public final static String HEADER_VALUE_APPLICATION_JSON = "application/json";
    public static final int HTTP_CONNECT_TIMEOUT = 10;
    public static final int HTTP_READ_TIMEOUT = 15;
    public static final int HTTP_WRITE_TIMEOUT = 15;

    public static final long SPLASH_DURATION = 1800;

    /* UI */
    public static final float DOUBLE_TAP_ZOOM_SCALE = 1.0f;
    public static final int PHRASEBOOK_FILTER_DEBOUNCE = 250;

    /* GMS */
    public static final float MAX_LOCATION_ACCURACY = 150.0f;
    public static final int APP_LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public static final int LOCATION_UPDATE_INTERVAL_MILLISECONDS = 7000;
    public static final int LOCATION_FASTEST_UPDATE_INTERVAL_MILLISECONDS = LOCATION_UPDATE_INTERVAL_MILLISECONDS / 2;
    public static final int GEO_CODING_TIMEOUT_MILLISECONDS = LOCATION_FASTEST_UPDATE_INTERVAL_MILLISECONDS;

    /* Phrasebook */
    public static final String PHRASEBOOK_CSV_ASSET_PATH = "phrasebook.csv";
    public static final int DEFAULT_FOREIGN_LANG_SPINNER_POSITION = 0;
    public static final int LANGUAGES_HEADER_ROW_POSITION = 0;
    public static final int ORIGINAL_LANG_COLUMN_POSITION = 0;

    /* Contact */
    public static final String CONTACT_CSV_ASSET_PATH = "contact_data.csv";
}
