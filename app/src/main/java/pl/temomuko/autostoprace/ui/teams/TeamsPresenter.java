package pl.temomuko.autostoprace.ui.teams;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by szymen on 2016-02-05.
 */
public class TeamsPresenter extends DrawerBasePresenter<DrawerMvpView> {

    @Inject
    public TeamsPresenter(DataManager dataManager) {
        super(dataManager);
    }
}
