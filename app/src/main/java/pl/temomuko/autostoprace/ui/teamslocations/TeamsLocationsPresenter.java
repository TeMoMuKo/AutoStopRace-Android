package pl.temomuko.autostoprace.ui.teamslocations;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import retrofit2.Response;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rafał Naniewicz on 01.04.2016.
 */
public class TeamsLocationsPresenter extends DrawerBasePresenter<TeamsLocationsMvpView> {

    private final static String TAG = TeamsLocationsPresenter.class.getSimpleName();
    private final ErrorHandler mErrorHandler;
    private Subscription mLoadTeamSubscription;
    private Subscription mLoadAllTeamsSubscription;
    private CompositeSubscription mSubscriptions;

    @Inject
    public TeamsLocationsPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        super(dataManager);
        mErrorHandler = errorHandler;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(TeamsLocationsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        if (mLoadTeamSubscription != null) mLoadTeamSubscription.unsubscribe();
        if (mLoadAllTeamsSubscription != null) mLoadAllTeamsSubscription.unsubscribe();
        super.detachView();
    }

    public void loadAllTeams() {
        getMvpView().setProgress(true);
        if (mLoadAllTeamsSubscription != null) mLoadAllTeamsSubscription.unsubscribe();
        mLoadAllTeamsSubscription = mDataManager.getAllTeams()
                .flatMap(HttpStatus::requireOk)
                .map(Response::body)
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(this::handleAllTeams,
                        this::handleError);
    }

    private void handleAllTeams(List<Team> teams) {
        if (mLoadTeamSubscription == null || mLoadTeamSubscription.isUnsubscribed())
            getMvpView().setProgress(false);
        getMvpView().setHints(teams);
        LogUtil.i(TAG, teams.get(0).getName());
    }

    public void handleTeamCharSequence(CharSequence charSequence) {
        int teamId = Integer.valueOf(charSequence.toString());
        getMvpView().displayTeam(teamId);
    }

    public void loadTeam(int teamId) {
        getMvpView().setProgress(true);
        if (mLoadTeamSubscription != null) mLoadTeamSubscription.unsubscribe();
        mLoadTeamSubscription = mDataManager.getTeamLocationRecordsFromServer(teamId)
                .flatMap(HttpStatus::requireOk)
                .map(Response::body)
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(this::handleTeamLocation,
                        this::handleError);
    }

    private void handleTeamLocation(List<LocationRecord> locations) {
        if (mLoadAllTeamsSubscription == null || mLoadAllTeamsSubscription.isUnsubscribed())
            getMvpView().setProgress(false);
        getMvpView().setLocations(locations);
    }

    private void handleError(Throwable throwable) {
        getMvpView().showError(throwable.toString());
        LogUtil.e(TAG, throwable.toString());
    }
}
