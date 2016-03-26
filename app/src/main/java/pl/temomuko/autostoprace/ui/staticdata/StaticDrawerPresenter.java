package pl.temomuko.autostoprace.ui.staticdata;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by Szymon Kozak on 2016-03-24.
 */
public class StaticDrawerPresenter extends DrawerBasePresenter<DrawerMvpView> {

    @Inject
    public StaticDrawerPresenter(DataManager dataManager) {
        super(dataManager);
    }
}
