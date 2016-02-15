package pl.temomuko.autostoprace.ui.main;

import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.event.RemovedLocationEvent;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.EventUtil;
import pl.temomuko.autostoprace.util.RxUtil;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by szymen on 2016-01-09.
 */
public class MainPresenter extends DrawerBasePresenter<MainMvpView> {

    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

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
        if (mSubscriptions != null && !mSubscriptions.isUnsubscribed()) {
            mSubscriptions.unsubscribe();
        }
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
                        mDataManager.clearUserData();
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
                mDataManager.getTeamLocationsFromDatabase()
                        .subscribe(this::updateLocationsView));
    }

    private void downloadLocationsFromServer() {
        getMvpView().setProgress(true);
        mSubscriptions.add(mDataManager.getTeamLocationsFromServer()
                .compose(RxUtil.applySchedulers())
                .flatMap(mDataManager::syncWithDatabase)
                .subscribe(locations -> {
                            updateLocationsView(locations);
                            postUnsentLocationsToServer();
                        }, this::handleError
                ));
    }

    private void updateLocationsView(List<Location> locations) {
        Collections.reverse(locations);
        if (locations.isEmpty()) getMvpView().showEmptyInfo();
        else getMvpView().updateLocationsList(locations);
        getMvpView().setProgress(false);
    }

    public void goToPostLocation() {
        getMvpView().dismissNoFineLocationPermissionSnackbar();
        if (getMvpView().hasLocationPermission()) {
            getMvpView().startPostActivity();
        } else {
            getMvpView().compatRequestFineLocationPermission(FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void handlePermissionResult(int requestCode, int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMvpView().startPostActivity();
                } else {
                    getMvpView().showNoFineLocationPermissionSnackbar();
                }
                break;
            }
        }
    }

    public void postUnsentLocationsToServer() {
        mSubscriptions.add(mDataManager.getUnsentLocations()
                .flatMap((Location unsentLocation) -> mDataManager.postLocationToServer(unsentLocation)
                        .compose(RxUtil.applySchedulers())
                        .flatMap(mDataManager::handlePostLocationResponse)
                        .flatMap(mDataManager::saveSentLocationToDatabase)
                        .toCompletable().endWith(mDataManager.deleteUnsentLocation(unsentLocation))
                        .toCompletable().endWith(Observable.just(unsentLocation)))
                .subscribe(removedLocation -> {
                            EventUtil.postSticky(new RemovedLocationEvent(removedLocation));
                            Log.i("EventUtil", "Removed: " + removedLocation.toString());
                        },
                        this::handleError));
    }

    private void handleError(Throwable throwable) {
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }
}
