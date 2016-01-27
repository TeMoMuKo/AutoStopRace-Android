package pl.temomuko.autostoprace.ui.main;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by szymen on 2016-01-09.
 */
public class MainPresenter extends BasePresenter<MainMvpView> {

    private DataManager mDataManager;
    private Subscription mSubscription;
    private static String TAG = "MainPresenter";

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
                .onErrorReturn(throwable -> {
                    handleRetrofitError(throwable);
                    return new ArrayList<>();
                })
                .switchMap(mDataManager::saveLocationsToDatabase)
                .subscribe(locations -> {
                    if (locations.isEmpty()) getMvpView().showEmptyInfo();
                    else getMvpView().updateLocationsList(locations);
                }, throwable -> {
                    handleError();
                });
    }

    private void handleRetrofitError(Throwable throwable) {
        Context context = (Context) getMvpView();
        getMvpView().showApiError(new ErrorHandler(context, throwable).getMessage());
    }

    private void handleError() {
        Context context = (Context) getMvpView();
        getMvpView().showApiError(context.getString(R.string.error_unknown));
    }
}
