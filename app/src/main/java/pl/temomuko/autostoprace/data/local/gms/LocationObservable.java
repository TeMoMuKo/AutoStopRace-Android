package pl.temomuko.autostoprace.data.local.gms;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Rafał Naniewicz on 10.02.2016.
 */
public class LocationObservable extends GmsBaseObservable<Location> implements LocationListener {

    private final LocationRequest mLocationRequest;
    private Subscriber<? super Location> mSubscriber;

    public static Observable<Location> createObservable(Context context, LocationRequest locationRequest) {
        return Observable.create(new LocationObservable(context, locationRequest));
    }

    private LocationObservable(Context context, LocationRequest locationRequest) {
        super(context, LocationServices.API);
        mLocationRequest = locationRequest;
    }

    @SuppressWarnings("ResourceType")
    @Override
    protected void onApiClientReady(GoogleApiClient apiClient, Subscriber<? super Location> subscriber) {
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, mLocationRequest, this);
        mSubscriber = subscriber;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!mSubscriber.isUnsubscribed()) {
            mSubscriber.onNext(location);
        }
    }

    @Override
    protected void onUnsubscribe(GoogleApiClient apiClient) {
        if (apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }
    }
}
