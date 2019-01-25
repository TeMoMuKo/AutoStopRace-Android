package pl.temomuko.autostoprace.ui.settings;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private static final String TAG = SettingsPresenter.class.getSimpleName();

    private final DataManager mDataManager;
    private Subscription mSubscription;

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
        //todo
//        mSubscription = mDataManager.signOut()
//                .compose(RxUtil.applyIoSchedulers())
//                .subscribe(response -> {
//                    LogUtil.i(TAG, response.body().toString());
//                }, throwable -> {
//                    LogUtil.e(TAG, throwable.getMessage());
//                });
//        mDataManager.clearUserData().subscribe();
//        getMvpView().showLogoutMessage();
//        getMvpView().disablePostLocationShortcut();
//        getMvpView().startLauncherActivity();
    }
}
