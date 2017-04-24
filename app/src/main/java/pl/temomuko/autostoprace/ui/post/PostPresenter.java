package pl.temomuko.autostoprace.ui.post;

import android.app.Activity;
import android.location.Address;
import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import javax.inject.Inject;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.local.gms.ApiClientConnectionFailedException;
import pl.temomuko.autostoprace.data.local.photo.ImageSourceType;
import pl.temomuko.autostoprace.data.local.photo.PhotoShadowActivity;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.AddressUtil;
import pl.temomuko.autostoprace.util.LocationSettingsUtil;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observer;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Szymon Kozak on 2016-01-30.
 */
public class PostPresenter extends BasePresenter<PostMvpView> {

    private static final String TAG = PostPresenter.class.getSimpleName();

    private final DataManager mDataManager;
    private final CompositeSubscription mLocationSubscriptions;
    private final CompositeSubscription mPhotoSubscriptions;
    private Address mLatestAddress;
    private boolean mIsLocationSettingsStatusForResultCalled = false;
    private boolean mIsLocationSaved;

    private final Subject<Void, Void> mVoidResumePublishSubject = PublishSubject.create();

    @Inject
    public PostPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        mLocationSubscriptions = new CompositeSubscription();
        mPhotoSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(PostMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        mLocationSubscriptions.unsubscribe();
        mPhotoSubscriptions.unsubscribe();
        super.detachView();
    }

    public void onResume() {
        mVoidResumePublishSubject.onNext(null);
    }

    public void tryToSaveLocation(String message, Uri currentPhotoUri) {
        if (mLatestAddress == null) {
            getMvpView().showNoLocationEstablishedError();
        } else {
            saveLocation(message, currentPhotoUri);
        }
    }

    public void startLocationService() {
        mLatestAddress = null;
        getMvpView().clearCurrentLocation();
        if (mDataManager.hasFineLocationPermission()) {
            checkLocationSettings();
        } else {
            getMvpView().compatRequestFineLocationPermission();
        }
    }

    public void handleLocationSettingsDialogResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            startLocationUpdates();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            getMvpView().finishWithInadequateSettingsWarning();
        }
    }

    public void handleLocationPermissionResult(boolean permissionGranted) {
        if (permissionGranted) {
            checkLocationSettings();
        } else {
            getMvpView().finishWithInadequateSettingsWarning();
        }
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


    public void requestPhoto(ImageSourceType imageSourceType) {
        //// FIXME: 24.04.2017 really hacky solution for rotation bug
        mPhotoSubscriptions.add(
                mDataManager.requestPhoto(imageSourceType)
                        .flatMap(uri -> mVoidResumePublishSubject.asObservable(), ((uri, voidValue) -> uri))
                        .subscribe(getPhotoObserver())
        );
    }

    public void checkForUnreceivedPhoto() {
        mPhotoSubscriptions.add(
                mDataManager.getPhotoObservable()
                        .subscribe(getPhotoObserver())
        );
    }

    /* Private helper methods */

    private void saveLocation(String message, Uri photoUri) {
        if (!mIsLocationSaved) {
            mIsLocationSaved = true;
            LocationRecord locationRecordToSend = new LocationRecord(mLatestAddress.getLatitude(),
                    mLatestAddress.getLongitude(),
                    message,
                    AddressUtil.getAddressString(mLatestAddress),
                    mLatestAddress.getCountryName(),
                    mLatestAddress.getCountryCode(),
                    photoUri);
            mDataManager.saveUnsentLocationRecordToDatabase(locationRecordToSend)
                    .compose(RxUtil.applyCompletableIoSchedulers())
                    .subscribe(this::handleLocationSaved);
        }
    }

    private void handleLocationSaved() {
        getMvpView().showSuccessInfo();
        getMvpView().closeActivityWithSuccessCode();
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
        LogUtil.i("Address update: ", address.toString());
        mLatestAddress = address;
        String addressString = AddressUtil.getAddressString(address);
        if (addressString != null) {
            getMvpView().updateCurrentLocation(address.getLatitude(), address.getLongitude(), addressString);
        } else {
            getMvpView().updateCurrentLocation(address.getLatitude(), address.getLongitude());
        }
    }

    private Observer<Uri> getPhotoObserver() {
        return new Observer<Uri>() {
            @Override
            public void onCompleted() {
                //no-op
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, String.format("Error while taking photo: %s", e.getLocalizedMessage()));
                if (!(e instanceof PhotoShadowActivity.ImageActionCanceledException)) {
                    getMvpView().showErrorWhileTakingPhoto();
                }
            }

            @Override
            public void onNext(Uri uri) {
                mDataManager.markPhotoAsReceived();
                getMvpView().setPhoto(uri);
            }
        };
    }

    public void removePhoto() {
        getMvpView().clearPhoto();
    }
}