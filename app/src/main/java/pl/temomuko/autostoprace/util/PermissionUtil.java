package pl.temomuko.autostoprace.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Rafał Naniewicz on 17.02.2016.
 */
public final class PermissionUtil {

    public static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

    private PermissionUtil() {
        throw new AssertionError();
    }

    public static void requestFineLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_PERMISSION_REQUEST_CODE);
    }

    public static boolean wasFineLocationPermissionGranted(int requestCode, int[] grantResults) {
        return requestCode == PermissionUtil.FINE_LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
