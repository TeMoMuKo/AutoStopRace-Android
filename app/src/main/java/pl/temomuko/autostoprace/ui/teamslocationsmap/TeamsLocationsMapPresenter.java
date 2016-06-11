package pl.temomuko.autostoprace.ui.teamslocationsmap;

import android.app.Activity;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.TeamNotFoundException;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Rafał Naniewicz on 01.04.2016.
 */
public class TeamsLocationsMapPresenter extends DrawerBasePresenter<TeamsLocationsMapMvpView> {

    private static final String TAG = TeamsLocationsMapPresenter.class.getSimpleName();

    private final ErrorHandler mErrorHandler;
    private Subscription mLoadAllTeamsSubscription;
    private Subscription mLoadTeamSubscription;
    private RxCacheHelper<Response<List<Team>>> mRxAllTeamsCacheHelper;
    private RxCacheHelper<Response<List<LocationRecord>>> mRxTeamLocationsCacheHelper;

    @Inject
    public TeamsLocationsMapPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        super(dataManager);
        mErrorHandler = errorHandler;
    }

    @Override
    public void attachView(TeamsLocationsMapMvpView mvpView) {
        super.attachView(mvpView);
        if (mRxAllTeamsCacheHelper.isCached()) {
            continueCachedAllTeamsRequest();
        }
        if (mRxTeamLocationsCacheHelper.isCached()) {
            continueCachedTeamLocationsRequest();
        }
    }

    @Override
    public void detachView() {
        if (mLoadTeamSubscription != null) mLoadTeamSubscription.unsubscribe();
        if (mLoadAllTeamsSubscription != null) mLoadAllTeamsSubscription.unsubscribe();
        super.detachView();
    }

    public void setupRxCacheHelper(Activity activity,
                                   RxCacheHelper<Response<List<Team>>> rxAllTeamsCacheHelper,
                                   RxCacheHelper<Response<List<LocationRecord>>> rxTeamLocationsCacheHelper) {
        mRxAllTeamsCacheHelper = rxAllTeamsCacheHelper;
        mRxAllTeamsCacheHelper.setup(activity);

        mRxTeamLocationsCacheHelper = rxTeamLocationsCacheHelper;
        mRxTeamLocationsCacheHelper.setup(activity);
    }

    public void loadAllTeams() {
        mRxAllTeamsCacheHelper.cache(
                mDataManager.getAllTeams()
                        .flatMap(HttpStatus::requireOk)
                        .compose(RxUtil.applyIoSchedulers())
        );
        continueCachedAllTeamsRequest();
    }

    public void loadTeam(String text) {
        try {
            int teamId = Integer.parseInt(text);
            loadTeam(teamId);
        } catch (NumberFormatException e) {
            getMvpView().showInvalidFormatError();
        }
    }

    public void loadTeam(int teamNumber) {
        getMvpView().clearCurrentTeamLocations();
        mRxTeamLocationsCacheHelper.cache(
                mDataManager.getTeamLocationRecordsFromServer(teamNumber)
                        .flatMap(listResponse -> listResponse.code() == HttpStatus.NOT_FOUND ?
                                Observable.error(new TeamNotFoundException(listResponse)) :
                                Observable.just(listResponse))
                        .flatMap(HttpStatus::requireOk)
                        .compose(RxUtil.applyIoSchedulers())
        );
        continueCachedTeamLocationsRequest();
    }

    /* Private helper methods */

    private void continueCachedAllTeamsRequest() {
        getMvpView().setAllTeamsProgress(true);
        if (mLoadAllTeamsSubscription != null) mLoadAllTeamsSubscription.unsubscribe();
        mLoadAllTeamsSubscription = mRxAllTeamsCacheHelper.getRestoredCachedObservable()
                .subscribe(
                        teams -> {
                            handleAllTeams(teams.body());
                            mRxAllTeamsCacheHelper.clearCache();
                        },
                        this::handleLoadAllTeamsError
                );
    }

    private void handleAllTeams(List<Team> teams) {
        getMvpView().setAllTeamsProgress(false);
        getMvpView().setHints(teams);
    }

    private void handleLoadAllTeamsError(Throwable throwable) {
        mRxAllTeamsCacheHelper.clearCache();
        getMvpView().setAllTeamsProgress(false);
        getMvpView().showError(mErrorHandler.getMessage(throwable));
    }

    private void continueCachedTeamLocationsRequest() {
        getMvpView().setTeamProgress(true);
        if (mLoadTeamSubscription != null) mLoadTeamSubscription.unsubscribe();
        mLoadTeamSubscription = mRxTeamLocationsCacheHelper.getRestoredCachedObservable()
                .subscribe(
                        listResponse -> {
                            handleTeamLocation(listResponse.body());
                            mRxTeamLocationsCacheHelper.clearCache();
                        },
                        this::handleLoadTeamError
                );
    }

    private void handleTeamLocation(List<LocationRecord> locations) {
        getMvpView().setTeamProgress(false);
        getMvpView().setLocations(locations);
        if (locations.isEmpty()) {
            getMvpView().showNoLocationRecordsInfo();
        }
    }

    private void handleLoadTeamError(Throwable throwable) {
        mRxTeamLocationsCacheHelper.clearCache();
        getMvpView().showError(mErrorHandler.getMessage(throwable));
        getMvpView().setTeamProgress(false);
    }
}
