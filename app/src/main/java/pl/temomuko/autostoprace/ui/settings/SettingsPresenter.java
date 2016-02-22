package pl.temomuko.autostoprace.ui.settings;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.RxUtil;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private DataManager mDataManager;
    private Subscription mSubscription;
    private final static String TAG = SettingsPresenter.class.getSimpleName();

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
        if (mSubscription != null) mSubscription.unsubscribe();
        super.detachView();
    }

    public void setupLogoutPreference() {
        boolean isAuth = mDataManager.isLoggedWithToken();
        getMvpView().setupLogoutPreferenceEnabled(isAuth);
        if (isAuth) {
            String username = mDataManager.getCurrentUser().getUsername();
            getMvpView().setupUserLogoutPreferenceSummary(username);
        } else {
            getMvpView().setupGuestLogoutPreferenceSummary();
        }
    }

    public void logout() {
        mSubscription = mDataManager.signOut()
                .compose(RxUtil.applySchedulers())
                .subscribe(response -> {
                    LogUtil.i(TAG, response.body().toString());
                }, throwable -> {
                    LogUtil.e(TAG, throwable.getMessage());
                });
        mDataManager.clearUserData().subscribe();
        getMvpView().showLogoutMessage();
        getMvpView().startLauncherActivity();
    }
}
