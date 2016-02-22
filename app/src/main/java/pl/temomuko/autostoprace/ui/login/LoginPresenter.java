package pl.temomuko.autostoprace.ui.login;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.RxUtil;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private DataManager mDataManager;
    private ErrorHandler mErrorHandler;
    private Subscription mSubscription;
    private Observable<Response<SignInResponse>> mCurrentRequestObservable;
    private final static String TAG = "LoginPresenter";

    @Inject
    public LoginPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        mDataManager = dataManager;
        mErrorHandler = errorHandler;
    }

    public void setCurrentRequestObservable(Observable<Response<SignInResponse>> observable) {
        mCurrentRequestObservable = observable;
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        super.attachView(mvpView);
        continueRequest();
    }

    private void continueRequest() {
        if (mCurrentRequestObservable != null) {
            subscribeCurrentRequestObservable();
        }
    }

    @Override
    public void detachView() {
        if (mSubscription != null) mSubscription.unsubscribe();
        getMvpView().saveCurrentRequestObservable(mCurrentRequestObservable);
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
        mCurrentRequestObservable = mDataManager.signIn(email, password)
                .compose(RxUtil.applySchedulers())
                .cache();
        subscribeCurrentRequestObservable();
    }

    private void subscribeCurrentRequestObservable() {
        mSubscription = mCurrentRequestObservable
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

    private void clearCurrentRequestObservable() {
        mCurrentRequestObservable = null;
    }

    private void handleError(Throwable throwable) {
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }
}
