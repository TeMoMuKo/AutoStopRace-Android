package pl.temomuko.autostoprace.ui.launcher;

import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by szymen on 2016-01-22.
 */
public interface LauncherMvpView extends DrawerMvpView {

    void startLoginActivity();

    void startContactActivity();
}
