package pl.temomuko.autostoprace.ui.main;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.local.gms.ApiClientConnectionFailedException;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.api.repository.Authenticator;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import retrofit2.HttpException;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Szymon Kozak on 2016-01-09.
 */
public class MainPresenter extends DrawerBasePresenter<MainMvpView> {

    private static final String TAG = MainPresenter.class.getSimpleName();
    private static final long MAX_TIME_WITHOUT_LOCATIONS_SYNC_IN_SECS = 60 * 60;

    private final ErrorHandler mErrorHandler;
    private final CompositeSubscription mSubscriptions;
    private Subscription mLoadLocationsSubscription;
    private final Authenticator authenticator;
    private boolean mIsLocationSettingsStatusForResultCalled = false;

    @Inject
    public MainPresenter(DataManager dataManager, ErrorHandler errorHandler, Authenticator authenticator) {
        super(dataManager);
        mErrorHandler = errorHandler;
        this.authenticator = authenticator;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        mSubscriptions.unsubscribe();
        if (mLoadLocationsSubscription != null) mLoadLocationsSubscription.unsubscribe();
        super.detachView();
    }

    public void checkAuth() {
        if (isAuthorized()) {
            validateToken();
        } else {
            getMvpView().startLauncherActivity();
        }
    }

    public void syncLocationsIfRecentlyNotSynced() {
        if (mDataManager.getLastLocationSyncTimestamp() < System.currentTimeMillis() / 1000 - MAX_TIME_WITHOUT_LOCATIONS_SYNC_IN_SECS) {
            getMvpView().startLocationSyncService();
        }
    }

    public boolean isAuthorized() {
        return mDataManager.isLoggedWithToken();
    }

    public void loadLocations() {
        getMvpView().setProgress(true);
        if (mLoadLocationsSubscription != null) mLoadLocationsSubscription.unsubscribe();
        mLoadLocationsSubscription = mDataManager.getTeamLocationRecordsFromDatabase()
                .compose(RxUtil.applySingleIoSchedulers())
                .subscribe(this::setLocationsView,
                        this::handleLoadLocationsError);
    }

    public void goToPostLocation() {
        getMvpView().dismissWarning();
        if (mDataManager.hasFineLocationPermission()) {
            checkLocationSettings();
        } else {
            getMvpView().compatRequestFineLocationPermission();
        }
    }

    public void checkLocationSettings() {
        if (!mIsLocationSettingsStatusForResultCalled) {
            mSubscriptions.add(mDataManager.checkLocationSettings()
                    .compose(RxUtil.applyIoSchedulers())
                    .subscribe(this::handleLocationSettings,
                            this::handleGmsError));
        }
    }

    public void handleLocationSettingsDialogResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            getMvpView().startPostActivity();
        } else {
            getMvpView().showLocationSettingsWarning();
        }
        getMvpView().setGoToPostLocationHandled();
    }

    public void handleFineLocationRequestPermissionResult(boolean permissionGranted) {
        if (permissionGranted) {
            checkLocationSettings();
        } else {
            getMvpView().showNoFineLocationPermissionWarning();
            getMvpView().setGoToPostLocationHandled();
        }
    }

    public void setIsLocationSettingsStatusForResultCalled(boolean isLocationSettingsStatusForResultCalled) {
        mIsLocationSettingsStatusForResultCalled = isLocationSettingsStatusForResultCalled;
    }

    public int getCurrentUserTeamNumber() {
        return mDataManager.getCurrentUser().getTeamNumber();
    }

    public void handleSyncServiceError(Throwable throwable) {
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }

    public void goToPhrasebook() {
        getMvpView().startPhrasebookActivity();
    }

    /* Private helper methods */

    private void validateToken() {
        mSubscriptions.add(authenticator.validateToken()
                .toObservable()
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(user -> mDataManager.saveUser(user),
                        throwable -> {
                            if (throwable instanceof HttpException && ((HttpException) throwable).response().code() == 401) {
                                mDataManager.clearUserData().subscribe();
                                getMvpView().showSessionExpiredError();
                                getMvpView().disablePostLocationShortcut();
                                getMvpView().startLoginActivity();
                            }
                        }));
    }

    private void setLocationsView(List<LocationRecord> locationRecords) {
        if (locationRecords.isEmpty()) {
            getMvpView().showEmptyInfo();
        } else {
            getMvpView().updateLocationRecordsList(locationRecords);
        }
        getMvpView().setProgress(false);
    }

    private void handleLocationSettings(LocationSettingsResult locationSettingsResult) {
        final int statusCode = locationSettingsResult.getStatus().getStatusCode();
        switch (statusCode) {
            case LocationSettingsStatusCodes.SUCCESS:
                getMvpView().startPostActivity();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                getMvpView().onUserResolvableLocationSettings(locationSettingsResult.getStatus());
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Locations settings are inadequate and cannot be resolved by Dialog. " +
                        "Mostly Airplane mode is on!");
                getMvpView().showInadequateSettingsWarning();
                break;
        }
        getMvpView().setGoToPostLocationHandled();
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
            getMvpView().setGoToPostLocationHandled();
        }
    }

    private void handleLoadLocationsError(Throwable throwable) {
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }

    public void goToLocationsMap() {
        getMvpView().startTeamLocationsMapActivity();
    }
}
