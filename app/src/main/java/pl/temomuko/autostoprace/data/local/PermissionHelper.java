package pl.temomuko.autostoprace.data.local;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by Rafał Naniewicz on 17.02.2016.
 */
@Singleton
public class PermissionHelper {

    private final Context mContext;

    @Inject
    public PermissionHelper(@AppContext Context context) {
        mContext = context;
    }

    public boolean hasFineLocationPermission() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
