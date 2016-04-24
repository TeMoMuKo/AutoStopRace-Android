package pl.temomuko.autostoprace.ui.login.resetpass;

import android.app.Activity;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.ResetPassResponse;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import retrofit2.Response;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-03-19.
 */
public class ResetPassPresenter extends BasePresenter<ResetPassMvpView> {

    private final DataManager mDataManager;
    private final ErrorHandler mErrorHandler;
    private Subscription mSubscription;
    private RxCacheHelper<Response<ResetPassResponse>> mRxResetCacheHelper;

    @Inject
    public ResetPassPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        mDataManager = dataManager;
        mErrorHandler = errorHandler;
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

    public void setupRxCacheHelper(Activity activity, RxCacheHelper<Response<ResetPassResponse>> helper) {
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
        return mErrorHandler.isEmailValid(email);
    }

    private void requestResetPassword(String email) {
        mRxResetCacheHelper.cache(
                mDataManager.resetPassword(email)
                        .flatMap(HttpStatus::requireOk)
                        .compose(RxUtil.applyIoSchedulers())
        );
        continueCachedRequest();
    }

    private void continueCachedRequest() {
        getMvpView().setProgress(true);
        mSubscription = mRxResetCacheHelper.getRestoredCachedObservable()
                .subscribe(response -> {
                    clearCurrentRequestObservable();
                    String email = response.body().getUser().getEmail();
                    getMvpView().showSuccessInfo(email);
                    getMvpView().finish();
                }, this::handleError, this::stopProgress);
    }

    private void clearCurrentRequestObservable() {
        mRxResetCacheHelper.clearCache();
    }

    private void handleError(Throwable throwable) {
        clearCurrentRequestObservable();
        getMvpView().setProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }

    private void stopProgress() {
        getMvpView().setProgress(false);
    }

    public void cancelResetPassRequest() {
        if (mSubscription != null) mSubscription.unsubscribe();
        clearCurrentRequestObservable();
    }
}
