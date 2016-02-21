package pl.temomuko.autostoprace.ui.post;

import android.app.Activity;
import android.location.Address;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.local.gms.ApiClientConnectionFailedException;
import pl.temomuko.autostoprace.data.local.gms.GmsLocationHelper;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.PermissionUtil;
import pl.temomuko.autostoprace.util.RxUtil;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Szymon Kozak on 2016-01-30.
 */
public class PostPresenter extends BasePresenter<PostMvpView> {

    private static final int UPDATE_INTERVAL_MILLISECONDS = 10000;
    private static final int FASTEST_UPDATE_INTERVAL_MILLISECONDS = UPDATE_INTERVAL_MILLISECONDS / 2;
    private static final int LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    private DataManager mDataManager;
    private CompositeSubscription mSubscriptions;
    private CompositeSubscription mLocationSubscriptions;
    private Subscription geocodingSubscription;

    private Location mLatestLocation;
    private LocationRequest mLocationRequest;
    private boolean mIsLocationSaved;

    private final static String TAG = PostPresenter.class.getSimpleName();

    @Inject
    public PostPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        mLocationRequest = new LocationRequest()
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MILLISECONDS)
                .setInterval(UPDATE_INTERVAL_MILLISECONDS)
                .setPriority(LOCATION_ACCURACY);
        mSubscriptions = new CompositeSubscription();
        mLocationSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(PostMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        mSubscriptions.unsubscribe();
        mLocationSubscriptions.unsubscribe();
        if (geocodingSubscription != null) geocodingSubscription.unsubscribe();
        super.detachView();
    }

    public void setLatestLocation(Location latestLocation) {
        mLatestLocation = latestLocation;
    }

    public void tryToSaveLocation(String message) {
        if (mLatestLocation == null) {
            getMvpView().showNoLocationEstablishedError();
        } else {
            saveLocation(message);
        }
    }

    private void saveLocation(String message) {
        if (!mIsLocationSaved) {
            double latitude = mLatestLocation.getLatitude();
            double longitude = mLatestLocation.getLongitude();
            LocationRecord locationRecordToSend = new LocationRecord(latitude, longitude, message);
            mDataManager.saveUnsentLocationRecordToDatabase(locationRecordToSend)
                    .compose(RxUtil.applySchedulers())
                    .subscribe();
            setLocationSaved();
            getMvpView().showSuccessInfo();
            getMvpView().startMainActivity();
        }
    }

    private void setLocationSaved() {
        mIsLocationSaved = true;
    }

    public void startLocationService() {
        if (mDataManager.hasFineLocationPermission()) {
            checkLocationSettings();
        } else {
            getMvpView().compatRequestFineLocationPermission();
        }
    }

    private void checkLocationSettings() {
        if (!getMvpView().isLocationSettingsStatusDialogCalled()) {
            mLocationSubscriptions.add(mDataManager.checkLocationSettings(GmsLocationHelper.APP_LOCATION_REQUEST)
                    .subscribe(this::handleLocationSettings,
                            this::handleGmsError));
        }
    }

    private void handleLocationSettings(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                getMvpView().onUserResolvableLocationSettings(status);
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                LogUtil.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    public void handleLocationSettingsDialogResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            startLocationUpdates();
        } else {
            getMvpView().finishWithInadequateSettingsWarning();
        }
    }

    private void handleGmsError(Throwable throwable) {
        if (throwable instanceof ApiClientConnectionFailedException) {
            ConnectionResult connectionResult =
                    ((ApiClientConnectionFailedException) throwable).getConnectionResult();
            if (connectionResult.hasResolution()) {
                getMvpView().onGmsConnectionResultResolutionRequired(connectionResult);
            } else {
                getMvpView().onGmsConnectionResultNoResolution(connectionResult.getErrorCode());
            }
        }
    }

    public void handleLocationPermissionResult(int requestCode, int[] grantResults) {
        if (PermissionUtil.wasFineLocationPermissionGranted(requestCode, grantResults)) {
            checkLocationSettings();
        } else {
            getMvpView().finishWithInadequateSettingsWarning();
        }
    }

    private void startLocationUpdates() {
        mLocationSubscriptions.add(mDataManager.getDeviceLocation(mLocationRequest)
                .subscribe(this::handleLocationUpdate, this::handleGmsError));
    }

    private void handleLocationUpdate(Location location) {
        if (mLatestLocation == null) {
            getMvpView().displayGPSFixAcquired();
        }
        mLatestLocation = location;
        getMvpView().updateCurrentLocationCords(mLatestLocation.getLatitude(), mLatestLocation.getLongitude());
        LogUtil.i("Location update :",
                Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude()));
        geocodeAddress();
    }

    private void geocodeAddress() {
        if (geocodingSubscription != null) geocodingSubscription.unsubscribe();
        geocodingSubscription = mDataManager.getAddressFromCoordinates(mLatestLocation.getLatitude(),
                mLatestLocation.getLongitude())
                .subscribe(
                        this::handleGeocodedAddress,
                        this::handleGeocodeError
                );
    }

    private void handleGeocodedAddress(Address address) {
        getMvpView().updateCurrentLocationAddress(getAddressString(address));
    }

    private String getAddressString(Address address) {
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i != address.getMaxAddressLineIndex()) {
                addressStringBuilder.append("\n");
            }
        }
        return addressStringBuilder.toString();
    }

    private void handleGeocodeError(Throwable throwable) {
        //// TODO: 21.02.2016 handle errors
    }

    public void stopLocationService() {
        mLocationSubscriptions.clear();
    }

    public void handleGpsStatusChange() {
        stopLocationService();
        startLocationService();
    }
}
