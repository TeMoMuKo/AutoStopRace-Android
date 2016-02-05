package pl.temomuko.autostoprace.ui.schedule;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by szymen on 2016-02-05.
 */
public class SchedulePresenter extends DrawerBasePresenter<DrawerMvpView> {

    @Inject
    public SchedulePresenter(DataManager dataManager) {
        super(dataManager);
    }
}
