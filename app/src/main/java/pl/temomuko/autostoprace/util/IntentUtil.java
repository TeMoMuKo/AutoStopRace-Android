package pl.temomuko.autostoprace.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;

/**
 * Created by Rafał Naniewicz on 17.02.2016.
 */
public final class IntentUtil {

    private static final Intent sAirplaneModeSettingsIntent =
            new Intent().setAction(Settings.ACTION_AIRPLANE_MODE_SETTINGS);

    private IntentUtil() {
        throw new AssertionError();
    }

    public static void goToAppSettings(Context context) {
        Intent appSettingsIntent = new Intent();
        appSettingsIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(appSettingsIntent);
    }

    public static void goToAirplaneModeSettings(Context context) {
        context.startActivity(sAirplaneModeSettingsIntent);
    }

    public static boolean isAirplaneModeSettingActivityAvailable(Context context) {
        return isIntentCallable(context, sAirplaneModeSettingsIntent);
    }

    public static void startGmsConnectionResultForResolution(Activity activity,
                                                             ConnectionResult connectionResult,
                                                             int requestCode) {
        try {
            connectionResult.startResolutionForResult(activity, requestCode);
        } catch (IntentSender.SendIntentException e) {
            LogUtil.e("Intent sender exception", e.getMessage());
        }
    }

    public static void startGmsStatusForResolution(Activity activity, Status status, int requestCode) {
        try {
            status.startResolutionForResult(activity, requestCode);
        } catch (IntentSender.SendIntentException e) {
            LogUtil.e("Intent sender exception", e.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void addClearBackStackIntentFlags(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }

    public static void shareLocationsMap(Context context, String teamNumberValue) {
        String destUrl = Constants.URL_MAP;
        String shareMapTitle = context.getString(R.string.msg_teams_locations);
        if (!teamNumberValue.isEmpty()) {
            destUrl += (Constants.URL_MAP_TEAM_NUMBER_PATH + "/" + teamNumberValue);
            shareMapTitle = context.getString(R.string.msg_team_locations, teamNumberValue);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, destUrl);
        context.startActivity(Intent.createChooser(intent, shareMapTitle));
    }

    private static boolean isIntentCallable(Context context, Intent intent) {
        return intent.resolveActivity(context.getPackageManager()) != null;
    }
}
