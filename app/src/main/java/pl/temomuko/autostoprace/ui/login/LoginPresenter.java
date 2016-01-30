package pl.temomuko.autostoprace.ui.login;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatus;
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
    private final static String TAG = "LoginPresenter";

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
                .subscribe(this::processLoginResponse, this::handleError);
    }

    private void processLoginResponse(Response<SignInResponse> response) {
        if (response.code() == HttpStatus.OK) {
            mDataManager.saveAuthorizationResponse(response);
            getMvpView().goToMainActivity();
        } else {
            handleResponseError(response);
        }
    }

    private void handleResponseError(Response response) {
        Context context = (Context) getMvpView();
        ErrorHandler handler = new ErrorHandler(context, response);
        getMvpView().showApiError(handler.getMessage());
    }

    private void handleError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
        Context context = (Context) getMvpView();
        getMvpView().showApiError(context.getString(R.string.error_unknown));
    }
}
