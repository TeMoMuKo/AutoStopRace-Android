package pl.temomuko.autostoprace.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by Rafał on 17.03.2016.
 */
public final class LocationSettingsUtil {

    private LocationSettingsUtil() {
        throw new AssertionError();
    }

    /**
     * BUGFIX Google api pre 4.2 : when wifi is off google api dialog always return RESULT_CANCEL,
     * also different behaviour of GMS on older devices often cause user to disagree with turning on wifi.
     * https://github.com/googlesamples/android-play-location/issues/19
     */
    public static int getApiDependentResultCode(int resultCode, Intent locationSettingRequestIntentData) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            LocationSettingsStates locationSettingsStates =
                    LocationSettingsStates.fromIntent(locationSettingRequestIntentData);
            if (locationSettingsStates != null && locationSettingsStates.isGpsUsable())
                resultCode = Activity.RESULT_OK;
        }
        return resultCode;
    }

    /**
     * BUGFIX Google api pre 4.2 : when wifi is off google api dialog always return RESULT_CANCEL,
     * also different behaviour of GMS on older devices often cause user to disagree with turning on wifi.
     * https://github.com/googlesamples/android-play-location/issues/19
     */

    public static int getApiDependentStatusCode(LocationSettingsResult locationSettingsResult) {
        int statusCode = locationSettingsResult.getStatus().getStatusCode();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            LocationSettingsStates settingsStates = locationSettingsResult.getLocationSettingsStates();
            if (settingsStates != null && settingsStates.isGpsUsable())
                statusCode = LocationSettingsStatusCodes.SUCCESS;
        }
        return statusCode;
    }
}
