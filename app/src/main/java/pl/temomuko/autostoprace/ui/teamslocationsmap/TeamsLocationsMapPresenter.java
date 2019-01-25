package pl.temomuko.autostoprace.ui.teamslocationsmap;

import android.app.Activity;
import android.net.Uri;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.local.LocationsViewMode;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.TeamNotFoundException;
import pl.temomuko.autostoprace.data.remote.api.repository.LocationsRepository;
import pl.temomuko.autostoprace.data.remote.api.repository.TeamsRepository;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map.LocationRecordClusterItem;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall.WallItem;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall.WallItemsCreator;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.Single;
import rx.Subscription;

public class TeamsLocationsMapPresenter extends DrawerBasePresenter<TeamsLocationsMapMvpView> {

    private static final String TAG = TeamsLocationsMapPresenter.class.getSimpleName();

    private final ErrorHandler errorHandler;
    private final WallItemsCreator wallItemsCreator;
    private Subscription loadAllTeamsSubscription;
    private Subscription loadTeamSubscription;
    private Subscription handleClusterSubscription;
    private RxCacheHelper<List<Team>> rxAllTeamsCacheHelper;
    private RxCacheHelper<List<LocationRecord>> rxTeamLocationsCacheHelper;
    private TeamsRepository teamsRepository;
    private LocationsRepository locationsRepository;

    @Inject
    public TeamsLocationsMapPresenter(
            DataManager dataManager,
            TeamsRepository teamsRepository,
            ErrorHandler errorHandler,
            WallItemsCreator wallItemsCreator,
            LocationsRepository locationsRepository) {
        super(dataManager);
        this.errorHandler = errorHandler;
        this.wallItemsCreator = wallItemsCreator;
        this.teamsRepository = teamsRepository;
        this.locationsRepository = locationsRepository;
    }

    @Override
    public void attachView(TeamsLocationsMapMvpView mvpView) {
        super.attachView(mvpView);
        getMvpView().setLocationsViewMode(mDataManager.getLocationsViewMode());
        if (rxAllTeamsCacheHelper.isCached()) {
            continueCachedAllTeamsRequest();
        }
        if (rxTeamLocationsCacheHelper.isCached()) {
            continueCachedTeamLocationsRequest();
        }
    }

    @Override
    public void detachView() {
        if (loadTeamSubscription != null) loadTeamSubscription.unsubscribe();
        if (loadAllTeamsSubscription != null) loadAllTeamsSubscription.unsubscribe();
        if (handleClusterSubscription != null) handleClusterSubscription.unsubscribe();
        super.detachView();
    }

    public void setupRxCacheHelper(Activity activity,
                                   RxCacheHelper<List<Team>> rxAllTeamsCacheHelper,
                                   RxCacheHelper<List<LocationRecord>> rxTeamLocationsCacheHelper) {
        this.rxAllTeamsCacheHelper = rxAllTeamsCacheHelper;
        this.rxAllTeamsCacheHelper.setup(activity);

        this.rxTeamLocationsCacheHelper = rxTeamLocationsCacheHelper;
        this.rxTeamLocationsCacheHelper.setup(activity);
    }

    public void loadAllTeams() {
        rxAllTeamsCacheHelper.cache(
                teamsRepository.getAllTeams()
                        .toObservable()
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
        rxTeamLocationsCacheHelper.cache(
                locationsRepository.getTeamLocations(teamNumber)
                        .toObservable()
                        .compose(RxUtil.applyIoSchedulers())
        );
        continueCachedTeamLocationsRequest();
    }

    public void updateLocationsViewModeContent(LocationsViewMode mode) {
        mDataManager.setLocationsViewMode(mode);
        getMvpView().setWallVisible(mode == LocationsViewMode.WALL);
    }

    public void handleFullScreenPhotoRequest(Uri imageUri) {
        if (imageUri != null) {
            getMvpView().openFullscreenImage(imageUri);
        }
    }

    public void handleClusterMarkerClick(final Collection<LocationRecordClusterItem> clusterItems) {
        if (handleClusterSubscription != null) handleClusterSubscription.unsubscribe();

        handleClusterSubscription = Single.fromCallable(() -> ClasterUtil.getNewestClusterItem(clusterItems))
                .map(LocationRecordClusterItem::getImageUri)
                .toObservable()
                .filter(uri -> uri != null)
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(getMvpView()::openFullscreenImage,
                        throwable -> LogUtil.e(TAG, "Error occurred while looking for newest cluster:" + throwable.getMessage())
                );
    }

    public void recreateWall(List<LocationRecord> locations) {
        showLocationsForWall(locations);
    }

    /* Private helper methods */

    private void continueCachedAllTeamsRequest() {
        getMvpView().setAllTeamsProgress(true);
        if (loadAllTeamsSubscription != null) loadAllTeamsSubscription.unsubscribe();
        loadAllTeamsSubscription = rxAllTeamsCacheHelper.getRestoredCachedObservable()
                .subscribe(
                        teams -> {
                            handleAllTeams(teams);
                            rxAllTeamsCacheHelper.clearCache();
                        },
                        this::handleLoadAllTeamsError
                );
    }

    private void handleAllTeams(List<Team> teams) {
        getMvpView().setAllTeamsProgress(false);
        getMvpView().setHints(teams);
    }

    private void handleLoadAllTeamsError(Throwable throwable) {
        rxAllTeamsCacheHelper.clearCache();
        getMvpView().setAllTeamsProgress(false);
        getMvpView().showError(errorHandler.getMessage(throwable));
    }

    private void continueCachedTeamLocationsRequest() {
        getMvpView().setTeamProgress(true);
        if (loadTeamSubscription != null) loadTeamSubscription.unsubscribe();
        loadTeamSubscription = rxTeamLocationsCacheHelper.getRestoredCachedObservable()
                .subscribe(
                        locations -> {
                            handleTeamLocation(locations);
                            rxTeamLocationsCacheHelper.clearCache();
                        },
                        this::handleLoadTeamError
                );
    }

    private void handleTeamLocation(List<LocationRecord> locations) {
        showLocationsForMap(locations);
        showLocationsForWall(locations);
    }

    private void showLocationsForMap(List<LocationRecord> locations) {
        getMvpView().setTeamProgress(false);
        getMvpView().setLocationsForMap(locations);
        if (locations.isEmpty()) {
            getMvpView().showNoLocationRecordsInfoForMap();
        }
    }

    private void showLocationsForWall(List<LocationRecord> locations) {
        List<WallItem> wallItems = wallItemsCreator.createFromLocationRecords(locations);
        getMvpView().setWallItems(wallItems);
        if (locations.isEmpty()) {
            getMvpView().hideWallItems();
        }
    }

    private void handleLoadTeamError(Throwable throwable) {
        rxTeamLocationsCacheHelper.clearCache();
        getMvpView().showError(errorHandler.getMessage(throwable));
        getMvpView().setTeamProgress(false);
    }
}
