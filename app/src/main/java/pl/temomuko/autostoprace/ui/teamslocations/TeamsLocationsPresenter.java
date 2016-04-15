package pl.temomuko.autostoprace.ui.teamslocations;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
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
        getMvpView().setAllTeamsProgress(true);
        if (mLoadAllTeamsSubscription != null) mLoadAllTeamsSubscription.unsubscribe();
        mLoadAllTeamsSubscription = mDataManager.getAllTeams()
                .flatMap(HttpStatus::requireOk)
                .map(Response::body)
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(this::handleAllTeams,
                        this::handleLoadAllTeamsError);
    }

    private void handleAllTeams(List<Team> teams) {
        getMvpView().setAllTeamsProgress(false);
        getMvpView().setHints(teams);
        LogUtil.i(TAG, teams.get(0).getName());
    }

    private void handleLoadAllTeamsError(Throwable throwable) {
        getMvpView().setAllTeamsProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
        LogUtil.e(TAG, mErrorHandler.getMessage(throwable));
    }

    public void loadTeam(int teamId) {
        getMvpView().setTeamProgress(true);
        if (mLoadTeamSubscription != null) mLoadTeamSubscription.unsubscribe();
        mLoadTeamSubscription = mDataManager.getTeamLocationRecordsFromServer(teamId)
                .flatMap(HttpStatus::requireOk)
                .map(Response::body)
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(this::handleTeamLocation,
                        this::handleLoadTeamError);
    }

    public void loadTeam(String text) {
        try {
            int teamId = Integer.parseInt(text);
            loadTeam(teamId);
        } catch (NumberFormatException e) {
            getMvpView().showInvalidFormatError();
        }
    }

    private void handleTeamLocation(List<LocationRecord> locations) {
        getMvpView().setTeamProgress(false);
        getMvpView().setLocations(locations);
    }

    private void handleLoadTeamError(Throwable throwable) {
        if (throwable instanceof StandardResponseException) {
            int code = ((StandardResponseException) throwable).getResponse().code();
            if (code == HttpStatus.NOT_FOUND) {
                getMvpView().showTeamNotFoundError();
            }
        } else {
            getMvpView().showError(mErrorHandler.getMessage(throwable));
        }
        getMvpView().setTeamProgress(false);
        LogUtil.e(TAG, mErrorHandler.getMessage(throwable));
    }
}
