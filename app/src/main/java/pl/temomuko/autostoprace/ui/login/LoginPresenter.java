package pl.temomuko.autostoprace.ui.login;

import android.app.Activity;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.api.repository.Authenticator;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private final DataManager mDataManager;
    private final ErrorHandler mErrorHandler;
    private Subscription mSubscription;
    private RxCacheHelper<User> mRxLoginCacheHelper;
    private final Authenticator authenticator;

    @Inject
    public LoginPresenter(DataManager dataManager, ErrorHandler errorHandler, Authenticator authenticator) {
        mDataManager = dataManager;
        mErrorHandler = errorHandler;
        this.authenticator = authenticator;
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

    public void setupRxCacheHelper(Activity activity, RxCacheHelper<User> helper) {
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
                authenticator.authorize(email, password)
                        .toObservable()
                        .compose(RxUtil.applyIoSchedulers())
        );
        continueCachedRequest();
    }

    private void continueCachedRequest() {
        mSubscription = mRxLoginCacheHelper.getRestoredCachedObservable()
                .subscribe(user -> {
                    clearCurrentRequestObservable();
                    mDataManager.saveUser(user);
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