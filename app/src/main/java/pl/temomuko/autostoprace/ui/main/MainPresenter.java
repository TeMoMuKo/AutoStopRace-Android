package pl.temomuko.autostoprace.ui.main;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatus;
import pl.temomuko.autostoprace.util.RxUtil;
import retrofit2.Response;
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
                .subscribe(this::processValidateTokenResponse, this::handleError));
    }

    private void processValidateTokenResponse(Response<SignInResponse> response) {
        if (response.code() == HttpStatus.OK) {
            mDataManager.saveAuthorizationResponse(response);
        } else if (response.code() == HttpStatus.UNAUTHORIZED) {
            mDataManager.clearUserData();
            getMvpView().showSessionExpiredError();
            getMvpView().startLoginActivity();
        }
    }

    public void loadLocationsFromDatabase() {
        mSubscriptions.add(
                mDataManager.getTeamLocationsFromDatabase()
                        .subscribe(this::handleLocationList));
    }

    public void loadLocationsFromServer() {
        getMvpView().setProgress(true);
        mSubscriptions.add(mDataManager.getTeamLocationsFromServer()
                .compose(RxUtil.applySchedulers())
                .subscribe(this::processLocationsResponse, this::handleError));
    }

    private void processLocationsResponse(Response<List<Location>> response) {
        if (response.code() == HttpStatus.OK) {
            mDataManager.saveAndEmitLocationsFromDatabase(response.body())
                    .subscribe(this::handleLocationList);
        } else {
            handleStandardResponseError(response);
        }
    }

    private void handleLocationList(List<Location> locations) {
        if (locations.isEmpty()) getMvpView().showEmptyInfo();
        else getMvpView().updateLocationsList(locations);
        getMvpView().setProgress(false);
    }

    public void goToPostLocation() {
        getMvpView().startPostActivity();
    }

    private void handleStandardResponseError(Response response) {
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessageFromResponse(response));
    }

    private void handleError(Throwable throwable) {
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessageFromRetrofitThrowable(throwable));
    }
}
