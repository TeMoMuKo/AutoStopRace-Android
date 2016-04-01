package pl.temomuko.autostoprace.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Rafał Naniewicz on 17.02.2016.
 */
public final class PermissionUtil {

    private PermissionUtil() {
        throw new AssertionError();
    }

    public static void requestFineLocationPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                requestCode);
    }

    public static boolean wasAllGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
