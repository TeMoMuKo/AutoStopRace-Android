package pl.temomuko.autostoprace.ui.login.resetpass;

import android.app.Activity;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.api.repository.Authenticator;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-03-19.
 */
public class ResetPassPresenter extends BasePresenter<ResetPassMvpView> {

    private final Authenticator authenticator;
    private final ErrorHandler errorHandler;
    private Subscription mSubscription;
    private RxCacheHelper<Object> mRxResetCacheHelper;

    @Inject
    public ResetPassPresenter(Authenticator authenticator, ErrorHandler errorHandler) {
        this.authenticator = authenticator;
        this.errorHandler = errorHandler;
    }

    @Override
    public void attachView(ResetPassMvpView mvpView) {
        super.attachView(mvpView);
        if (mRxResetCacheHelper.isCached()) {
            continueCachedRequest();
        }
    }

    @Override
    public void detachView() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.detachView();
    }

    public void setupRxCacheHelper(Activity activity, RxCacheHelper<Object> helper) {
        mRxResetCacheHelper = helper;
        mRxResetCacheHelper.setup(activity);
    }

    public void resetPassword(String email) {
        setupValidationHints(email);
        if (isEmailValid(email)) {
            getMvpView().setProgress(true);
            requestResetPassword(email);
        }
    }

    private void setupValidationHints(String email) {
        getMvpView().setInvalidEmailValidationError(!isEmailValid(email));
    }

    private boolean isEmailValid(String email) {
        return errorHandler.isEmailValid(email);
    }

    private void requestResetPassword(String email) {
        mRxResetCacheHelper.cache(
                authenticator.resetPassword(email)
                        .toObservable()
                        .compose(RxUtil.applyIoSchedulers())
        );
        continueCachedRequest();
    }

    private void continueCachedRequest() {
        getMvpView().setProgress(true);
        mSubscription = mRxResetCacheHelper.getRestoredCachedObservable()
                .subscribe(response -> {
                    clearCurrentRequestObservable();
                    getMvpView().showSuccessInfo();
                    getMvpView().finish();
                }, this::handleError, this::stopProgress);
    }

    private void clearCurrentRequestObservable() {
        mRxResetCacheHelper.clearCache();
    }

    private void handleError(Throwable throwable) {
        clearCurrentRequestObservable();
        getMvpView().setProgress(false);
        getMvpView().showError(errorHandler.getMessage(throwable));
    }

    private void stopProgress() {
        getMvpView().setProgress(false);
    }

    public void cancelResetPassRequest() {
        if (mSubscription != null) mSubscription.unsubscribe();
        clearCurrentRequestObservable();
    }
}
