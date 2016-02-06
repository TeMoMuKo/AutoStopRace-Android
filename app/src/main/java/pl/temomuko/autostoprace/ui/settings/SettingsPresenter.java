package pl.temomuko.autostoprace.ui.settings;

import android.util.Log;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.RxUtil;
import rx.Subscription;

/**
 * Created by szymen on 2016-02-05.
 */
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private DataManager mDataManager;
    private Subscription mSubscription;
    private static final String TAG = "SettingsPresenter";

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SettingsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        super.detachView();
    }

    public void setupLogoutPreference() {
        boolean isAuth = mDataManager.isLoggedWithToken();
        String username = mDataManager.getCurrentUser().getUsername();
        getMvpView().setupLogoutPreferenceEnabled(isAuth);
        getMvpView().setupLogoutPreferenceSummary(isAuth, username);
    }

    public void logout() {
        mSubscription = mDataManager.signOut()
                .compose(RxUtil.applySchedulers())
                .subscribe(response -> {
                    Log.i(TAG, response.body().toString());
                }, throwable -> {
                    Log.i(TAG, throwable.getMessage());
                });
        mDataManager.clearUserData();
        getMvpView().showLogoutMessage();
        getMvpView().startLauncherActivity();
    }
}
