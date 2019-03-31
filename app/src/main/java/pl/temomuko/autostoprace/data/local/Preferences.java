package pl.temomuko.autostoprace.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.domain.model.User;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by Szymon Kozak on 2016-01-09.
 */

@Singleton
public class Preferences {

    public static final String PREF_FILE_NAME = "asr_pref_file";
    public static final String PREF_AUTH_TOKEN = "auth_token";
    public static final String PREF_CURRENT_PHRASEBOOK_LANGUAGE = "pref_current_phrasebook_language";
    public static final String PREF_LOGOUT = "pref_logout";
    public static final String PREF_LOCATION_SYNC_TIMESTAMP = "pref_location_sync_timestamp";
    public static final String PREF_LOCATIONS_VIEW_MODE = "pref_locations_view_mode";
    private static final String PREF_CURRENT_USER_JSON = "pref_current_user_json";
    private static final String PREF_SCHEDULE_URL = "pref_schedule_url";
    private static final String PREF_CAMPUS_MAP_URL = "pref_campus_map_url";

    private final SharedPreferences mPrefs;

    @Inject
    public Preferences(@AppContext Context context) {
        mPrefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setAuthAccessToken(String token) {
        mPrefs.edit().putString(PREF_AUTH_TOKEN, token).apply();
    }

    public String getAuthAccessToken() {
        return mPrefs.getString(PREF_AUTH_TOKEN, "");
    }

    public void clearAuth() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(PREF_AUTH_TOKEN);
        editor.remove(PREF_CURRENT_USER_JSON);
        editor.apply();
    }

    public void setCurrentUser(User user) {
        String userJson = new Gson().toJson(user);
        mPrefs.edit().putString(PREF_CURRENT_USER_JSON, userJson).apply();
    }

    public User getCurrentUser() throws JsonSyntaxException {
        String userJson = mPrefs.getString(PREF_CURRENT_USER_JSON, "");
        return new Gson().fromJson(userJson, User.class);
    }

    public void setRaceInfoScheduleUrl(String scheduleUrl) {
        mPrefs.edit().putString(PREF_SCHEDULE_URL, scheduleUrl).apply();
    }

    public String getRaceInfoScheduleUrl() {
        return mPrefs.getString(PREF_SCHEDULE_URL, "");
    }

    public void setRaceInfoCampusMapUrl(String scheduleUrl) {
        mPrefs.edit().putString(PREF_CAMPUS_MAP_URL, scheduleUrl).apply();
    }

    public String getRaceInfoCampusMapUrl() {
        return mPrefs.getString(PREF_CAMPUS_MAP_URL, "");
    }

    public void setCurrentPhrasebookLanguagePosition(int languagePosition) {
        mPrefs.edit().putInt(PREF_CURRENT_PHRASEBOOK_LANGUAGE, languagePosition).apply();
    }

    public int getCurrentPhrasebookLanguagePosition() {
        return mPrefs.getInt(PREF_CURRENT_PHRASEBOOK_LANGUAGE, Constants.DEFAULT_FOREIGN_LANG_SPINNER_POSITION);
    }

    public void setLastLocationsSyncTimestamp(long timestamp) {
        mPrefs.edit().putLong(PREF_LOCATION_SYNC_TIMESTAMP, timestamp).apply();
    }

    public long getLastLocationSyncTimestamp() {
        return mPrefs.getLong(PREF_LOCATION_SYNC_TIMESTAMP, 0);
    }

    public LocationsViewMode getLocationsViewMode() {
        int ordinal = mPrefs.getInt(PREF_LOCATIONS_VIEW_MODE, LocationsViewMode.MAP.ordinal());
        return LocationsViewMode.values()[ordinal];
    }

    public void setLocationsViewMode(LocationsViewMode mode) {
        mPrefs.edit().putInt(PREF_LOCATIONS_VIEW_MODE, mode.ordinal()).apply();
    }
}
