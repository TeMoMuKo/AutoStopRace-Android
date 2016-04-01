package pl.temomuko.autostoprace.ui.teamslocationmap;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;

/**
 * Created by Rafał Naniewicz on 01.04.2016.
 */
public class TeamsLocationsMapPresenter extends DrawerBasePresenter<TeamsLocationsMapActivity> {

    private final static String TAG = TeamsLocationsMapPresenter.class.getSimpleName();

    @Inject
    public TeamsLocationsMapPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void attachView(TeamsLocationsMapActivity mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
