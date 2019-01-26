package pl.temomuko.autostoprace.ui.base.drawer;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.domain.model.User;
import pl.temomuko.autostoprace.ui.base.BasePresenter;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public abstract class DrawerBasePresenter<T extends DrawerMvpView> extends BasePresenter<T> {

    protected final DataManager mDataManager;

    public DrawerBasePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void setupUserInfoInDrawer() {
        if (mDataManager.isLoggedWithToken()) {
            User currentUser = mDataManager.getCurrentUser();
            getMvpView().setupHeaderUsername(currentUser.getUsername());
            getMvpView().setupHeaderEmail(currentUser.getEmail());
            getMvpView().setupTeamNumberText(currentUser.getTeamNumber());
        }
    }
}
