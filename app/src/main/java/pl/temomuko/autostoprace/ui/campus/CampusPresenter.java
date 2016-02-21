package pl.temomuko.autostoprace.ui.campus;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class CampusPresenter extends DrawerBasePresenter<DrawerMvpView> {

    @Inject
    public CampusPresenter(DataManager dataManager) {
        super(dataManager);
    }
}
