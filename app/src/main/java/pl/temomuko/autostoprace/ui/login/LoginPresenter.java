package pl.temomuko.autostoprace.ui.login;

import android.app.Activity;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.remote.rxloader.RxCacheHelper;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.RxUtil;
import retrofit2.Response;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private DataManager mDataManager;
    private ErrorHandler mErrorHandler;
    private Subscription mSubscription;
    private RxCacheHelper<Response<SignInResponse>> mRxLoginCacheHelper;
    private final static String TAG = "LoginPresenter";

    @Inject
    public LoginPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        mDataManager = dataManager;
        mErrorHandler = errorHandler;
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        super.attachView(mvpView);
        mRxLoginCacheHelper = RxCacheHelper.create((Activity) getMvpView());
        mRxLoginCacheHelper.restore();
        continueCachedRequest();
    }

    private void continueCachedRequest() {
        if (mRxLoginCacheHelper.getCachedObservable() != null) {
            subscribeCurrentRequestObservable();
        }
    }

    @Override
    public void detachView() {
        mRxLoginCacheHelper.save();
        if (mSubscription != null) mSubscription.unsubscribe();
        super.detachView();
    }

    public void signIn(String email, String password) {
        setupValidationHints(email, password);
        if (isEmailValid(email) && isPasswordValid(password)) {
            getMvpView().setProgress(true);
            requestSignIn(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return mErrorHandler.isEmailValid(email);
    }

    private boolean isPasswordValid(String password) {
        return !password.isEmpty();
    }

    private void requestSignIn(String email, String password) {
        mRxLoginCacheHelper.cache(mDataManager.signIn(email, password)
                .compose(RxUtil.applySchedulers()));
        subscribeCurrentRequestObservable();
    }

    private void subscribeCurrentRequestObservable() {
        mSubscription = mRxLoginCacheHelper.getCachedObservable()
                .flatMap(response -> {
                    clearCurrentRequestObservable();
                    return mDataManager.handleLoginResponse(response);
                })
                .subscribe(response -> {
                    mDataManager.saveAuthorizationResponse(response);
                    getMvpView().startMainActivity();
                }, this::handleError, this::stopProgress);
    }

    private void stopProgress() {
        getMvpView().setProgress(false);
    }

    public void cancelSignInRequest() {
        if (mSubscription != null) mSubscription.unsubscribe();
        clearCurrentRequestObservable();
    }

    private void clearCurrentRequestObservable() {
        mRxLoginCacheHelper.clear();
    }

    private void setupValidationHints(String email, String password) {
        setupEmailHint(email);
        setupPasswordHint(password);
    }

    private void setupEmailHint(String email) {
        if (!isEmailValid(email)) {
            getMvpView().showInvalidEmailValidaionError();
        } else {
            getMvpView().hideEmailValidationError();
        }
    }

    private void setupPasswordHint(String password) {
        if (!isPasswordValid(password)) {
            getMvpView().showEmptyPasswordValidationError();
        } else {
            getMvpView().hidePasswordValidationError();
        }
    }

    private void handleError(Throwable throwable) {
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }
}
