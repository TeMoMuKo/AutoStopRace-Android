package pl.temomuko.autostoprace.ui.base.drawer;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by szymen on 2016-02-04.
 */
public interface DrawerMvpView extends MvpView {

    void setHeaderUsername(String username);

    void setHeaderEmail(String email);
}
