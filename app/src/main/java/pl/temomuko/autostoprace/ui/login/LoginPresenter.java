package pl.temomuko.autostoprace.ui.login;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
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
                    mDataManager.saveAuthorizationResponse(response);
                    getMvpView().goToMainActivity();
                }, throwable -> {
                    getMvpView().showError();
                });
    }
}
