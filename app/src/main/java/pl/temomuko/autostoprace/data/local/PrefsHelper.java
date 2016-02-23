package pl.temomuko.autostoprace.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Headers;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by Szymon Kozak on 2016-01-09.
 */

@Singleton
public class PrefsHelper {

    private SharedPreferences mPrefs;
    public final static String PREF_FILE_NAME = "asr_pref_file";
    public final static String PREF_AUTH_TOKEN = "auth_token";
    public final static String PREF_AUTH_CLIENT = "auth_client";
    public final static String PREF_AUTH_UID = "auth_uid";
    private static final String PREF_CURRENT_USER_JSON = "pref_current_user_json";

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
        editor.remove(PREF_CURRENT_USER_JSON);
        editor.apply();
    }

    public void setAuthorizationHeaders(Headers headers) {
        Map<String, List<String>> headersMap = headers.toMultimap();
        setAuthAccessToken(headersMap.get(Constants.HEADER_FIELD_TOKEN).get(0));
        setAuthClient(headersMap.get(Constants.HEADER_FIELD_CLIENT).get(0));
        setAuthUid(headersMap.get(Constants.HEADER_FIELD_UID).get(0));
    }

    public void setCurrentUser(User user) {
        String userJson = new Gson().toJson(user);
        mPrefs.edit().putString(PREF_CURRENT_USER_JSON, userJson).apply();
    }

    public User getCurrentUser() {
        String userJson = mPrefs.getString(PREF_CURRENT_USER_JSON, "");
        return new Gson().fromJson(userJson, User.class);
    }
}
