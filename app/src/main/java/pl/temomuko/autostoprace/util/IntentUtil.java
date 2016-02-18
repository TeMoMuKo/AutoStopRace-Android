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

/**
 * Created by Rafał Naniewicz on 17.02.2016.
 */
public final class IntentUtil {

    private IntentUtil() {
        throw new AssertionError();
    }

    public static void goToAppSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
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
}
