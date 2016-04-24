package pl.temomuko.autostoprace.ui.login;

import android.app.Activity;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import retrofit2.Response;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private final static String TAG = LoginPresenter.class.getSimpleName();

    private final DataManager mDataManager;
    private final ErrorHandler mErrorHandler;
    private Subscription mSubscription;
    private RxCacheHelper<Response<SignInResponse>> mRxLoginCacheHelper;

    @Inject
    public LoginPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        mDataManager = dataManager;
        mErrorHandler = errorHandler;
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        super.attachView(mvpView);
        if (mRxLoginCacheHelper.isCached()) {
            continueCachedRequest();
        }
    }

    @Override
    public void detachView() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.detachView();
    }

    public void setupRxCacheHelper(Activity activity, RxCacheHelper<Response<SignInResponse>> helper) {
        mRxLoginCacheHelper = helper;
        mRxLoginCacheHelper.setup(activity);
    }

    public void signIn(String email, String password) {
        setupValidationHints(email, password);
        if (isEmailValid(email) && isPasswordValid(password)) {
            getMvpView().setProgress(true);
            requestSignIn(email, password);
        }
    }

    public void cancelSignInRequest() {
        if (mSubscription != null) mSubscription.unsubscribe();
        clearCurrentRequestObservable();
    }

    private boolean isEmailValid(String email) {
        return mErrorHandler.isEmailValid(email);
    }

    private boolean isPasswordValid(String password) {
        return !password.isEmpty();
    }

    private void requestSignIn(String email, String password) {
        mRxLoginCacheHelper.cache(
                mDataManager.signIn(email, password)
                        .flatMap(HttpStatus::requireOk)
                        .compose(RxUtil.applyIoSchedulers())
        );
        continueCachedRequest();
    }

    private void continueCachedRequest() {
        mSubscription = mRxLoginCacheHelper.getRestoredCachedObservable()
                .subscribe(response -> {
                    clearCurrentRequestObservable();
                    mDataManager.saveAuthorizationResponse(response);
                    getMvpView().startMainActivity();
                }, this::handleError, this::stopProgress);
    }

    private void stopProgress() {
        getMvpView().setProgress(false);
    }

    private void clearCurrentRequestObservable() {
        mRxLoginCacheHelper.clearCache();
    }

    private void setupValidationHints(String email, String password) {
        getMvpView().setInvalidEmailValidationError(!isEmailValid(email));
        getMvpView().setInvalidPasswordValidationError(!isPasswordValid(password));
    }

    private void handleError(Throwable throwable) {
        clearCurrentRequestObservable();
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }
}