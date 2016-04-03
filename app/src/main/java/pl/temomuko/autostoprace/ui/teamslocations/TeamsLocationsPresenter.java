package pl.temomuko.autostoprace.ui.teamslocations;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;

/**
 * Created by Rafał Naniewicz on 01.04.2016.
 */
public class TeamsLocationsPresenter extends DrawerBasePresenter<TeamsLocationsMvpView> {

    private final static String TAG = TeamsLocationsPresenter.class.getSimpleName();

    @Inject
    public TeamsLocationsPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void attachView(TeamsLocationsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void handleTeamCharSequence(CharSequence charSequence) {
        int teamId = Integer.valueOf(charSequence.toString());
        getMvpView().displayTeam(teamId);
    }
}
