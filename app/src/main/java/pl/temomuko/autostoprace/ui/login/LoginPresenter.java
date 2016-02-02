package pl.temomuko.autostoprace.ui.login;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.content.ContentPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatus;
import retrofit2.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by szymen on 2016-01-22.
 */
public class LoginPresenter extends ContentPresenter<LoginMvpView> {

    private Subscription mSubscription;
    private final static String TAG = "LoginPresenter";

    @Inject
    public LoginPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        super(errorHandler, dataManager);
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        super.detachView();
    }

    public void signIn(String email, String password) {
        if (!mErrorHandler.isFormValid(email, password)) {
            getMvpView().showError(mErrorHandler.getValidErrorMessage(email, password));
        } else {
            getMvpView().setProgress(true);
            mSubscription = mDataManager.signIn(email, password)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.newThread())
                    .subscribe(this::processLoginResponse, this::handleError, this::stopProgress);
        }
    }

    public void cancelSignInRequest() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    private void stopProgress() {
        getMvpView().setProgress(false);
    }

    private void processLoginResponse(Response<SignInResponse> response) {
        if (response.code() == HttpStatus.OK) {
            mDataManager.saveAuthorizationResponse(response);
            getMvpView().startMainActivity();
        } else {
            handleStandardResponseError(response);
        }
    }
}
