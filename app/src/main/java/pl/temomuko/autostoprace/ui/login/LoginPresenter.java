package pl.temomuko.autostoprace.ui.login;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.NetworkUtil;
import retrofit.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by szymen on 2016-01-22.
 */
public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private Subscription mSubscription;
    private DataManager mDataManager;

    @Inject
    public LoginPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) mSubscription.unsubscribe();
    }

    public void signIn(String email, String password) {
        mSubscription = mDataManager.signIn(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.newThread())
                .subscribe(response -> {
                    if (NetworkUtil.isHttpOk(response.code())) {
                        mDataManager.saveAuthorizationResponse(response);
                        getMvpView().goToMainActivity();
                    } else {
                        handleResponseError(response);
                    }
                }, this::handleError);
    }

    private void handleResponseError(Response response) {
        Context context = (Context) getMvpView();
        ErrorHandler handler = new ErrorHandler(context, response);
        getMvpView().showApiError(handler.getMessage());
    }

    private void handleError(Throwable throwable) {
        Log.e("LoginPresenter", throwable.getMessage());
        Context context = (Context) getMvpView();
        getMvpView().showApiError(context.getString(R.string.error_unknown));
    }
}
