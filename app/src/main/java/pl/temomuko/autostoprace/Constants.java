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

    public static final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String APP_DATE_FORMAT = "d.LL";
    public static final String APP_TIME_FORMAT = "HH:mm";
    public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String MAP_FULL_DATE_FORMAT = "HH:mm dd.MM";
    public static final String SERVER_TIMEZONE = "UTC";

    public static final String GOOGLE_PLAY_BASE_URL = "http://play.google.com/store/apps/details?id=";

    public static final String LICENSES_ASSET_URI = "file:///android_asset/licenses.html";
    public static final String URL_MAP = "https://mapa.autostoprace.pl/";
    public static final String URL_MAP_TEAM_NUMBER_PATH = "team";

    public static final String HEADER_VALUE_APPLICATION_JSON = "application/json";
    public static final int HTTP_CONNECT_TIMEOUT = 10;
    public static final int HTTP_READ_TIMEOUT = 15;
    public static final int HTTP_WRITE_TIMEOUT = 20;

    /* UI */
    public static final float DOUBLE_TAP_ZOOM_SCALE = 1.0f;
    public static final int PHRASEBOOK_FILTER_DEBOUNCE = 250;
    public static final long SPLASH_DURATION = 1800;

    /* GMS */
    public static final float MAX_LOCATION_ACCURACY = 150.0f;
    public static final int APP_LOCATION_ACCURACY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
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
