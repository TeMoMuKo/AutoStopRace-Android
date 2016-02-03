package pl.temomuko.autostoprace.ui.main;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.content.ContentPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatus;
import pl.temomuko.autostoprace.util.RxUtil;
import retrofit2.Response;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by szymen on 2016-01-09.
 */
public class MainPresenter extends ContentPresenter<MainMvpView> {

    private final static String TAG = "MainPresenter";
    private CompositeSubscription mSubscriptions;

    @Inject
    public MainPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        super(errorHandler, dataManager);
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
        if (!mDataManager.isLoggedWithToken()) {
            getMvpView().startLauncherActivity();
        } else {
            validateToken();
        }
    }

    private void validateToken() {
        mSubscriptions.add(mDataManager.validateToken()
                .compose(RxUtil.applySchedulers())
                .subscribe(this::processValidateTokenResponse, this::handleError));
    }

    private void processValidateTokenResponse(Response<SignInResponse> response) {
        if (response.code() == HttpStatus.OK) {
            mDataManager.saveAuthorizationResponse(response);
        } else if (response.code() == HttpStatus.UNAUTHORIZED) {
            mDataManager.clearAuth();
            getMvpView().showSessionExpiredError();
            getMvpView().startLoginActivity();
        }
    }

    public void setupUserInfo() {
        getMvpView().showUser(mDataManager.getCurrentUser());
    }

    public void logout() {
        mSubscriptions.add(mDataManager.signOut()
                .compose(RxUtil.applySchedulers())
                .subscribe(response -> {
                    Log.i(TAG, response.body().toString());
                }, throwable -> {
                    Log.i(TAG, throwable.getMessage());
                }));
        mDataManager.clearAuth();
        getMvpView().showLogoutMessage();
        getMvpView().startLauncherActivity();
    }

    public void loadLocationsFromDatabase() {
        //TODO
    }

    public void loadLocationsFromServer() {
        mSubscriptions.add(mDataManager.getTeamLocationsFromServer()
                .compose(RxUtil.applySchedulers())
                .subscribe(this::processLocationsResponse, this::handleError));
    }

    private void processLocationsResponse(Response<List<Location>> response) {
        if (response.code() == HttpStatus.OK) {
            mDataManager.saveLocationsToDatabase(response.body())
                    .subscribe(locations -> {
                        if (locations.isEmpty()) getMvpView().showEmptyInfo();
                        else getMvpView().updateLocationsList(locations);
                    });
        } else {
            handleStandardResponseError(response);
        }
    }

    public void goToPostLocation() {
        getMvpView().startPostActivity();
    }
}
