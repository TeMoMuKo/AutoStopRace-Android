package pl.temomuko.autostoprace.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Headers;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by szymen on 2016-01-09.
 */

@Singleton
public class PrefsHelper {

    private SharedPreferences mPrefs;
    public final static String PREF_FILE_NAME = "asr_pref_file";

    public final static String PREF_AUTH_TOKEN = "auth_token";
    public final static String PREF_AUTH_CLIENT = "auth_client";
    public final static String PREF_AUTH_UID = "auth_uid";

    public final static String PREF_CURRENT_USER_FIRST_NAME = "current_user_first_name";
    public final static String PREF_CURRENT_USER_LAST_NAME = "current_user_last_name";
    public final static String PREF_CURRENT_USER_EMAIL = "current_user_email";
    public final static String PREF_CURRENT_USER_ID = "current_user_team_id";
    public final static String PREF_CURRENT_USER_TEAM_ID = "current_user_team_id";

    public final static String PREF_LOGOUT = "pref_logout";

    @Inject
    public PrefsHelper(@AppContext Context context) {
        mPrefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setAuthAccessToken(String token) {
        mPrefs.edit().putString(PREF_AUTH_TOKEN, token).apply();
    }

    public void setAuthUid(String uid) {
        mPrefs.edit().putString(PREF_AUTH_UID, uid).apply();
    }

    public void setAuthClient(String client) {
        mPrefs.edit().putString(PREF_AUTH_CLIENT, client).apply();
    }

    public String getAuthAccessToken() {
        return mPrefs.getString(PREF_AUTH_TOKEN, "");
    }

    public String getAuthClient() {
        return mPrefs.getString(PREF_AUTH_CLIENT, "");
    }

    public String getAuthUid() {
        return mPrefs.getString(PREF_AUTH_UID, "");
    }

    public void clearAuth() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(PREF_AUTH_TOKEN);
        editor.remove(PREF_AUTH_CLIENT);
        editor.remove(PREF_AUTH_UID);
        editor.apply();
        clearUser();
    }

    private void clearUser() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(PREF_CURRENT_USER_FIRST_NAME);
        editor.remove(PREF_CURRENT_USER_LAST_NAME);
        editor.remove(PREF_CURRENT_USER_EMAIL);
        editor.remove(PREF_CURRENT_USER_ID);
        editor.remove(PREF_CURRENT_USER_TEAM_ID);
        editor.apply();
    }

    public void setAuthorizationHeaders(Headers headers) {
        Map<String, List<String>> headersMap = headers.toMultimap();
        setAuthAccessToken(headersMap.get(Constants.HEADER_FIELD_TOKEN).get(0));
        setAuthClient(headersMap.get(Constants.HEADER_FIELD_CLIENT).get(0));
        setAuthUid(headersMap.get(Constants.HEADER_FIELD_UID).get(0));
    }

    public void setCurrentUser(User user) {
        mPrefs.edit().putString(PREF_CURRENT_USER_FIRST_NAME, user.getFirstName()).apply();
        mPrefs.edit().putString(PREF_CURRENT_USER_LAST_NAME, user.getLastName()).apply();
        mPrefs.edit().putString(PREF_CURRENT_USER_EMAIL, user.getEmail()).apply();
        mPrefs.edit().putInt(PREF_CURRENT_USER_ID, user.getId()).apply();
        mPrefs.edit().putInt(PREF_CURRENT_USER_TEAM_ID, user.getTeamId()).apply();
    }

    public User getCurrentUser() {
        String firstName = mPrefs.getString(PREF_CURRENT_USER_FIRST_NAME, "");
        String lastName = mPrefs.getString(PREF_CURRENT_USER_LAST_NAME, "");
        String email = mPrefs.getString(PREF_CURRENT_USER_EMAIL, "");
        int id = mPrefs.getInt(PREF_CURRENT_USER_ID, 0);
        int teamId = mPrefs.getInt(PREF_CURRENT_USER_TEAM_ID, 0);
        return new User(id, teamId, firstName, lastName, email);
    }
}
