package pl.temomuko.autostoprace.ui.main;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.event.RemovedLocationEvent;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.EventPoster;
import pl.temomuko.autostoprace.util.HttpStatus;
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
        getMvpView().startPostActivity();
    }

    public void postUnsentLocationsToServer() {
        mSubscriptions.add(mDataManager.getUnsentLocations()
                .compose(RxUtil.applySchedulers())
                .flatMap(location ->
                        mDataManager.postLocationToServer(location)
                                .compose(RxUtil.applySchedulers())
                                .flatMap(response ->
                                        mDataManager.processDeleteUnsentLocation(response, location)))
                .subscribe(removedLocation -> {
                    EventPoster.postSticky(new RemovedLocationEvent(removedLocation));
                    Log.i("EventPoster", removedLocation.toString());
                }, this::handleError));
    }

    private void handleError(Throwable throwable) {
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }
}
