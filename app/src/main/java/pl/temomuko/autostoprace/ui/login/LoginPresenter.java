package pl.temomuko.autostoprace.ui.login;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.content.ContentPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatus;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by szymen on 2016-01-22.
 */
public class LoginPresenter extends ContentPresenter<LoginMvpView> {

    private Subscription mSubscription;
    private Observable<Response<SignInResponse>> mCurrentRequestObservable;
    private final static String TAG = "LoginPresenter";

    @Inject
    public LoginPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        super(errorHandler, dataManager);
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
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
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
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.newThread());
        subscribeCurrentRequestObservable();
    }

    private void subscribeCurrentRequestObservable() {
        mSubscription = mCurrentRequestObservable
                .subscribe(this::processLoginResponse, this::handleError, this::stopProgress);
    }

    public void cancelSignInRequest() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        clearCurrentRequestObservable();
    }

    private boolean isLoginDataValid(String email, String password) {
        return mErrorHandler.isEmailValid(email) && mErrorHandler.isPasswordValid(password);
    }

    private void setupValidationHints(String email, String password) {
        setupEmailHint(email);
        setupPasswordHint(password);
    }

    private void setupPasswordHint(String password) {
        if (!mErrorHandler.isPasswordValid(password)) {
            getMvpView().showPasswordValidationError(mErrorHandler.getPasswordValidErrorMessage(password));
        } else {
            getMvpView().hidePasswordValidationError();
        }
    }

    private void setupEmailHint(String email) {
        if (!mErrorHandler.isEmailValid(email)) {
            getMvpView().showEmailValidationError(mErrorHandler.getEmailValidErrorMessage(email));
        } else {
            getMvpView().hideEmailValidationError();
        }
    }

    private void stopProgress() {
        getMvpView().setProgress(false);
    }

    private void processLoginResponse(Response<SignInResponse> response) {
        clearCurrentRequestObservable();
        if (response.code() == HttpStatus.OK) {
            mDataManager.saveAuthorizationResponse(response);
            getMvpView().startMainActivity();
        } else {
            handleStandardResponseError(response);
        }
    }

    @Override
    public void handleError(Throwable throwable) {
        super.handleError(throwable);
        clearCurrentRequestObservable();
    }

    private void clearCurrentRequestObservable() {
        mCurrentRequestObservable = null;
    }
}
