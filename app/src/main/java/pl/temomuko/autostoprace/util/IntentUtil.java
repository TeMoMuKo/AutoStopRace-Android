package pl.temomuko.autostoprace.util;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

/**
 * Created by szymen on 2016-02-18.
 */
public class IntentUtil {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void addClearBackStackIntentFlags(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }
}
