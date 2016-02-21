package pl.temomuko.autostoprace.ui.launcher;

import javax.inject.Inject;

import pl.temomuko.autostoprace.ui.base.BasePresenter;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class LauncherPresenter extends BasePresenter<LauncherMvpView> {

    @Inject
    public LauncherPresenter() {
    }

    public void goToLogin() {
        getMvpView().startLoginActivity();
    }

    public void goToContact() {
        getMvpView().startContactActivity();
    }
}
