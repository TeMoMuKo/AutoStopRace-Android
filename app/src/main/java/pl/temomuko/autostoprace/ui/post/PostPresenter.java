package pl.temomuko.autostoprace.ui.post;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import javax.inject.Inject;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.local.gms.ApiClientConnectionFailedException;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.LocationSettingsUtil;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.PermissionUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Szymon Kozak on 2016-01-30.
 */
public class PostPresenter extends BasePresenter<PostMvpView> {

    private final static String TAG = PostPresenter.class.getSimpleName();

    private DataManager mDataManager;
    private CompositeSubscription mSubscriptions;
    private CompositeSubscription mLocationSubscriptions;

    private boolean mIsLocationSettingsStatusForResultCalled = false;
    private Address mLatestAddress;

    private boolean mIsLocationSaved;

    @Inject
    public PostPresenter(DataManager dataManager) {
        mDataManager = dataManager;
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
        super.detachView();
    }

    public void tryToSaveLocation(String message) {
        if (mLatestAddress == null) {
            getMvpView().showNoLocationEstablishedError();
        } else {
            saveLocation(message);
        }
    }

    private void saveLocation(String message) {
        if (!mIsLocationSaved) {
            LocationRecord locationRecordToSend = new LocationRecord(mLatestAddress.getLatitude(),
                    mLatestAddress.getLongitude(),
                    message,
                    getAddressStringFromAddress(mLatestAddress),
                    mLatestAddress.getCountryName(),
                    mLatestAddress.getCountryCode());
            mDataManager.saveUnsentLocationRecordToDatabase(locationRecordToSend)
                    .compose(RxUtil.applyIoSchedulers())
                    .subscribe();
            setLocationSaved();
            getMvpView().showSuccessInfo();
            getMvpView().closeActivity();
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
        if (!mIsLocationSettingsStatusForResultCalled) {
            mLocationSubscriptions.add(mDataManager.checkLocationSettings()
                    .compose(RxUtil.applyIoSchedulers())
                    .subscribe(this::handleLocationSettings,
                            this::handleGmsError));
        }
    }

    private void handleLocationSettings(LocationSettingsResult locationSettingsResult) {
        final int statusCode = LocationSettingsUtil.getApiDependentStatusCode(locationSettingsResult);
        switch (statusCode) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                getMvpView().onUserResolvableLocationSettings(locationSettingsResult.getStatus());
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                LogUtil.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                getMvpView().finishWithInadequateSettingsWarning();
                break;
        }
    }

    public void handleLocationSettingsDialogResult(int resultCode, Intent data) {
        resultCode = LocationSettingsUtil.getApiDependentResultCode(resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            startLocationUpdates();
        } else if (resultCode == Activity.RESULT_CANCELED) {
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
        } else {
            LogUtil.e(TAG, throwable.toString());
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
        mLocationSubscriptions.add(mDataManager.getDeviceLocation()
                .filter(location -> location.getAccuracy() <= Constants.MAX_LOCATION_ACCURACY)
                .concatMap(location -> {
                    getMvpView().updateAccuracyInfo(location.getAccuracy());
                    LogUtil.i(TAG, "Accuracy: " + Float.toString(location.getAccuracy()));
                    return mDataManager.getAddressFromLocation(location);
                })
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(this::handleAddress, this::handleGmsError));
    }

    private void handleAddress(Address address) {
        LogUtil.i("Address update :", address.toString());
        mLatestAddress = address;
        String addressString = getAddressStringFromAddress(address);
        if (addressString != null) {
            getMvpView().updateCurrentLocation(address.getLatitude(), address.getLongitude(), addressString);
        } else {
            getMvpView().updateCurrentLocation(address.getLatitude(), address.getLongitude());
        }
    }

    private String getAddressStringFromAddress(Address address) {
        if (address.getMaxAddressLineIndex() == -1) {
            return null;
        }
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i != address.getMaxAddressLineIndex()) {
                addressStringBuilder.append(", ");
            }
        }
        return addressStringBuilder.toString();
    }

    public void stopLocationService() {
        mLocationSubscriptions.clear();
    }

    public void handleLocationSettingsStatusChange() {
        stopLocationService();
        startLocationService();
    }

    public void setIsLocationSettingsStatusForResultCalled(boolean isLocationSettingsStatusForResultCalled) {
        mIsLocationSettingsStatusForResultCalled = isLocationSettingsStatusForResultCalled;
    }

    public void setLatestAddress(Address latestAddress) {
        mLatestAddress = latestAddress;
    }
}
