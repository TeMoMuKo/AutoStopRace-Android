package pl.temomuko.autostoprace.ui.main;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.local.gms.ApiClientConnectionFailedException;
import pl.temomuko.autostoprace.data.local.gms.GmsLocationHelper;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.PermissionUtil;
import pl.temomuko.autostoprace.util.RxUtil;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by szymen on 2016-01-09.
 */
public class MainPresenter extends DrawerBasePresenter<MainMvpView> {

    private ErrorHandler mErrorHandler;
    private final static String TAG = "MainPresenter";
    private CompositeSubscription mSubscriptions;

    @Inject
    public MainPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        super(dataManager);
        mErrorHandler = errorHandler;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        if (mSubscriptions != null) mSubscriptions.unsubscribe();
        super.detachView();
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
                .compose(RxUtil.applySchedulers())
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

    public void loadLocations() {
        loadLocationsFromDatabase();
        downloadLocationsFromServer();
    }

    private void loadLocationsFromDatabase() {
        mSubscriptions.add(
                mDataManager.getTeamLocationRecordsFromDatabase()
                        .subscribe(this::updateLocationsView));
    }

    private void downloadLocationsFromServer() {
        getMvpView().setProgress(true);
        mSubscriptions.add(mDataManager.getTeamLocationRecordsFromServer()
                .compose(RxUtil.applySchedulers())
                .flatMap(mDataManager::syncWithDatabase)
                .subscribe(locationRecords -> {
                            updateLocationsView(locationRecords);
                            getMvpView().startPostService();
                        },
                        throwable -> {
                            handleError(throwable);
                            getMvpView().startPostService();
                        }));
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
        if (!getMvpView().isLocationSettingsStatusDialogCalled()) {
            mSubscriptions.add(mDataManager.checkLocationSettings(GmsLocationHelper.APP_LOCATION_REQUEST)
                    .subscribe(this::handleLocationSettings,
                            this::handleGmsError));
        }
    }

    private void handleLocationSettings(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                getMvpView().startPostActivity();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                getMvpView().onUserResolvableLocationSettings(status);
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    public void handleLocationSettingsDialogResult(int resultCode) {
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
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }
}
