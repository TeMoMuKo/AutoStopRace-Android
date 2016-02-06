package pl.temomuko.autostoprace.ui.base.drawer;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by szymen on 2016-02-04.
 */
public interface DrawerMvpView extends MvpView {

    void setupHeaderUsername(String username);

    void setupHeaderEmail(String email);

    void setupTeamCircle(int teamId);
}
