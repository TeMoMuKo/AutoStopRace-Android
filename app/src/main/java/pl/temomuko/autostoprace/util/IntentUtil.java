package pl.temomuko.autostoprace.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

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
}
