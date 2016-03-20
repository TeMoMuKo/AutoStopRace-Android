package pl.temomuko.autostoprace.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.local.gms.ApiClientConnectionFailedException;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.LocationSettingsUtil;
import pl.temomuko.autostoprace.util.PermissionUtil;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Szymon Kozak on 2016-01-09.
 */
public class MainPresenter extends DrawerBasePresenter<MainMvpView> {

    private final static String TAG = MainPresenter.class.getSimpleName();

    private ErrorHandler mErrorHandler;
    private CompositeSubscription mSubscriptions;
    private RxCacheHelper<List<LocationRecord>> mRxDownloadLocationsCacheHelper;
    private boolean mIsLocationSettingsStatusForResultCalled = false;

    @Inject
    public MainPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        super(dataManager);
        mErrorHandler = errorHandler;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
        if (mRxDownloadLocationsCacheHelper.isCached()) {
            continueCachedDownloadLocationsRequest();
        }
    }

    @Override
    public void detachView() {
        if (mSubscriptions != null) mSubscriptions.unsubscribe();
        super.detachView();
    }

    public void setupRxCacheHelper(Activity activity, RxCacheHelper<List<LocationRecord>> helper) {
        mRxDownloadLocationsCacheHelper = helper;
        mRxDownloadLocationsCacheHelper.setup(activity);
    }

    public void checkAuth() {
        if (isAuthorized()) {
            validateToken();
        } else {
            getMvpView().startLauncherActivity();
        }
    }

    public boolean isAuthorized() {
        return mDataManager.isLoggedWithToken();
    }

    private void validateToken() {
        mSubscriptions.add(mDataManager.validateToken()
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.code() == HttpStatus.OK) {
                        mDataManager.saveAuthorizationResponse(response);
                    } else if (response.code() == HttpStatus.UNAUTHORIZED) {
                        mDataManager.clearUserData().subscribe();
                        getMvpView().showSessionExpiredError();
                        getMvpView().startLoginActivity();
                    }
                }, this::handleError));
    }

    private void continueCachedDownloadLocationsRequest() {
        getMvpView().setProgress(true);
        mSubscriptions.add(mRxDownloadLocationsCacheHelper.getRestoredCachedObservable()
                .subscribe(locationRecords -> {
                            clearCurrentRequestObservable();
                            updateLocationsView(locationRecords);
                            getMvpView().setItemsExpandingEnabled(true);
                        },
                        this::handleError));
    }

    private void clearCurrentRequestObservable() {
        mRxDownloadLocationsCacheHelper.clearCache();
    }

    public void loadLocations() {
        loadLocationsFromDatabase();
        downloadLocationsFromServer();
    }

    public void loadLocationsFromDatabase() {
        mSubscriptions.add(
                mDataManager.getTeamLocationRecordsFromDatabase()
                        .compose(RxUtil.applyIoSchedulers())
                        .subscribe(this::updateLocationsView));
    }

    private void downloadLocationsFromServer() {
        mRxDownloadLocationsCacheHelper.cache(
                mDataManager.getTeamLocationRecordsFromServer()
                        .flatMap(mDataManager::syncWithDatabase)
                        .compose(RxUtil.applyIoSchedulers())
        );
        continueCachedDownloadLocationsRequest();
    }

    private void updateLocationsView(List<LocationRecord> locationRecords) {
        Collections.reverse(locationRecords);
        if (locationRecords.isEmpty()) getMvpView().showEmptyInfo();
        else getMvpView().updateLocationRecordsList(locationRecords);
        getMvpView().setProgress(false);
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
    }

    public void handleLocationSettingsDialogResult(int resultCode, Intent data) {
        resultCode = LocationSettingsUtil.getApiDependentResultCode(resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            getMvpView().startPostActivity();
        } else {
            getMvpView().showLocationSettingsWarning();
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

    public void handlePermissionResult(int requestCode, int[] grantResults) {
        if (PermissionUtil.wasFineLocationPermissionGranted(requestCode, grantResults)) {
            checkLocationSettings();
        } else {
            getMvpView().showNoFineLocationPermissionWarning();
        }
    }

    private void handleError(Throwable throwable) {
        clearCurrentRequestObservable();
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }

    public void setIsLocationSettingsStatusForResultCalled(boolean isLocationSettingsStatusForResultCalled) {
        mIsLocationSettingsStatusForResultCalled = isLocationSettingsStatusForResultCalled;
    }
}
