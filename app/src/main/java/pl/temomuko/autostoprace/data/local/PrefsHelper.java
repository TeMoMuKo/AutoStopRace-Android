package pl.temomuko.autostoprace.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by szymen on 2016-01-09.
 */

@Singleton
public class PrefsHelper {

    private SharedPreferences mPrefs;
    public final static String AUTH_TOKEN = "auth_token";
    public final static String AUTH_CLIENT = "auth_client";
    public final static String AUTH_UID = "auth_uid";

    public final static String CURRENT_USER_FIRST_NAME = "current_user_first_name";
    public final static String CURRENT_USER_LAST_NAME = "current_user_last_name";
    public final static String CURRENT_USER_EMAIL = "current_user_email";
    public final static String CURRENT_USER_ID = "current_user_team_id";
    public final static String CURRENT_USER_TEAM_ID = "current_user_team_id";

    @Inject
    public PrefsHelper(@AppContext Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setAuthAccessToken(String token) {
        mPrefs.edit().putString(AUTH_TOKEN, token).apply();
    }

    public void setAuthUid(String uid) {
        mPrefs.edit().putString(AUTH_UID, uid).apply();
    }

    public void setAuthClient(String client) {
        mPrefs.edit().putString(AUTH_CLIENT, client).apply();
    }

    public String getAuthAccessToken() {
        return mPrefs.getString(AUTH_TOKEN, "");
    }

    public String getAuthClient() {
        return mPrefs.getString(AUTH_CLIENT, "");
    }

    public String getAuthUid() {
        return mPrefs.getString(AUTH_UID, "");
    }

    public void clearAuth() {
        setAuthAccessToken("");
        setAuthClient("");
        setAuthUid("");
        setCurrentUser(new User(0, 0, "", "", ""));
    }

    public void setCurrentUser(User user) {
        mPrefs.edit().putString(CURRENT_USER_FIRST_NAME, user.getFirstName()).apply();
        mPrefs.edit().putString(CURRENT_USER_LAST_NAME, user.getLastName()).apply();
        mPrefs.edit().putString(CURRENT_USER_EMAIL, user.getEmail()).apply();
        mPrefs.edit().putInt(CURRENT_USER_ID, user.getId()).apply();
        mPrefs.edit().putInt(CURRENT_USER_TEAM_ID, user.getTeamId()).apply();
    }

    public User getCurrentUser() {
        String firstName = mPrefs.getString(CURRENT_USER_FIRST_NAME, "");
        String lastName = mPrefs.getString(CURRENT_USER_LAST_NAME, "");
        String email = mPrefs.getString(CURRENT_USER_EMAIL, "");
        int id = mPrefs.getInt(CURRENT_USER_ID, 0);
        int teamId = mPrefs.getInt(CURRENT_USER_TEAM_ID, 0);
        return new User(id, teamId, firstName, lastName, email);
    }
}
