package pl.temomuko.autostoprace.ui.settings;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.domain.repository.Authenticator;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private static final String TAG = SettingsPresenter.class.getSimpleName();

    private final DataManager dataManager;
    private final Authenticator authenticator;
    private Subscription mSubscription;

    @Inject
    public SettingsPresenter(DataManager dataManager, Authenticator authenticator) {
        this.dataManager = dataManager;
        this.authenticator = authenticator;
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
        boolean isAuth = dataManager.isLoggedWithToken();
        getMvpView().setupLogoutPreferenceEnabled(isAuth);
        if (isAuth) {
            String username = dataManager.getCurrentUser().getUsername();
            getMvpView().setupUserLogoutPreferenceSummary(username);
        } else {
            getMvpView().setupGuestLogoutPreferenceSummary();
        }
    }

    public void logout() {
        mSubscription = authenticator.logout()
                .compose(RxUtil.applyCompletableIoSchedulers())
                .subscribe(() -> {
                    LogUtil.i(TAG, "Logged out");
                }, throwable -> {
                    LogUtil.e(TAG, throwable.getMessage());
                });
        dataManager.clearUserData().subscribe();
        getMvpView().showLogoutMessage();
        getMvpView().disablePostLocationShortcut();
        getMvpView().startLauncherActivity();
    }
}
