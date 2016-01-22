package pl.temomuko.autostoprace.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by szymen on 2016-01-09.
 */

@Singleton
public class PrefsHelper {

    private SharedPreferences mPrefs;

    @Inject
    public PrefsHelper(@AppContext Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
