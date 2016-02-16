package pl.temomuko.autostoprace.ui.login;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.LoginValidator;
import pl.temomuko.autostoprace.util.RxUtil;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;

/**
 * Created by szymen on 2016-01-22.
 */
public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private DataManager mDataManager;
    private ErrorHandler mErrorHandler;
    private LoginValidator mLoginValidator;
    private Subscription mSubscription;
    private Observable<Response<SignInResponse>> mCurrentRequestObservable;
    private final static String TAG = "LoginPresenter";

    @Inject
    public LoginPresenter(DataManager dataManager, ErrorHandler errorHandler,
                          LoginValidator loginValidator) {
        mDataManager = dataManager;
        mErrorHandler = errorHandler;
        mLoginValidator = loginValidator;
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
        if (isLoginDataValid(email, password)) {
            getMvpView().setProgress(true);
            requestSignIn(email, password);
        }
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

    private boolean isLoginDataValid(String email, String password) {
        return mLoginValidator.isEmailValid(email) && mLoginValidator.isPasswordValid(password);
    }

    private void setupValidationHints(String email, String password) {
        setupEmailHint(email);
        setupPasswordHint(password);
    }

    private void setupPasswordHint(String password) {
        if (!mLoginValidator.isPasswordValid(password)) {
            getMvpView().showPasswordValidationError(mLoginValidator.getPasswordValidErrorMessage(password));
        } else {
            getMvpView().hidePasswordValidationError();
        }
    }

    private void setupEmailHint(String email) {
        if (!mLoginValidator.isEmailValid(email)) {
            getMvpView().showEmailValidationError(mLoginValidator.getEmailValidErrorMessage(email));
        } else {
            getMvpView().hideEmailValidationError();
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
