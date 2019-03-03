package pl.temomuko.autostoprace.ui.base.drawer;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by Szymon Kozak on 2016-02-04.
 */
public interface DrawerMvpView extends MvpView {

    void setupHeaderUsername(String username);

    void setupHeaderEmail(String email);

    void setupTeamNumberText(long teamNumber);
}
