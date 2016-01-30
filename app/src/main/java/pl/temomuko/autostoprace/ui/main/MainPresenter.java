package pl.temomuko.autostoprace.ui.main;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.ui.base.content.ContentPresenter;
import pl.temomuko.autostoprace.util.HttpStatus;
import retrofit.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by szymen on 2016-01-09.
 */
public class MainPresenter extends ContentPresenter<MainMvpView> {

    private DataManager mDataManager;
    private Subscription mSubscription;
    private final static String TAG = "MainPresenter";

    @Inject
    public MainPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) mSubscription.unsubscribe();
    }

    public void checkAuth() {
        if (!mDataManager.isLoggedWithToken()) {
            getMvpView().goToLauncherActivity();
        }
    }

    public void loadLocationsFromDatabase() {
        //TODO
    }

    public void setupUserInfo() {
        getMvpView().showUser(mDataManager.getCurrentUser());
    }

    public void logout() {
        mDataManager.signOut()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.newThread())
                .subscribe(response -> {
                    Log.i(TAG, response.body().toString());
                }, throwable -> {
                    Log.i(TAG, throwable.getMessage());
                });
        mDataManager.clearAuth();
        getMvpView().showLogoutMessage();
        getMvpView().goToLauncherActivity();
    }

    public void loadLocationsFromServer() {
        mSubscription = mDataManager.getTeamLocationsFromServer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.newThread())
                .subscribe(this::processLocationsResponse, this::handleError);
    }

    private void processLocationsResponse(Response<List<Location>> response) {
        if (response.code() == HttpStatus.OK) {
            mDataManager.saveLocationsToDatabase(response.body())
                    .subscribe(locations -> {
                        if (locations.isEmpty()) getMvpView().showEmptyInfo();
                        else getMvpView().updateLocationsList(locations);
                    });
        } else {
            handleResponseError(response);
        }
    }
}
