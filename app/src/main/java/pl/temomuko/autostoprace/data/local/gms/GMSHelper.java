package pl.temomuko.autostoprace.data.local.gms;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.injection.AppContext;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 15.02.2016.
 */
@Singleton
public class GmsHelper {

    private Context mContext;

    @Inject
    public GmsHelper(@AppContext Context context) {
        mContext = context;
    }

    public Observable<Location> getDeviceLocation(LocationRequest locationRequest) {
        return LocationObservable.createObservable(mContext, locationRequest);
    }

    public Observable<LocationSettingsResult> checkLocationSettings(LocationRequest locationRequest) {
        return ApiClientObservable.create(mContext, LocationServices.API)
                .flatMap(googleApiClient -> PendingResultObservable.create(
                        LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                                getLocationSettingsRequest(locationRequest))));
    }

    private LocationSettingsRequest getLocationSettingsRequest(LocationRequest locationRequest) {
        return new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
                .build();
    }
}
