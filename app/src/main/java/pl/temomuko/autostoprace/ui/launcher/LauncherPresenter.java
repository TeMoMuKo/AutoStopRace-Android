package pl.temomuko.autostoprace.ui.launcher;

import javax.inject.Inject;

import pl.temomuko.autostoprace.ui.base.BasePresenter;

/**
 * Created by szymen on 2016-01-22.
 */
public class LauncherPresenter extends BasePresenter<LauncherMvpView> {

    @Inject
    public LauncherPresenter() {
    }

    public void goToLogin() {
        getMvpView().startLoginActivity();
    }
}
