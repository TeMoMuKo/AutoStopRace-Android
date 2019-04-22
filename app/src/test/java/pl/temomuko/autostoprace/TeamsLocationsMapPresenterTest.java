package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.remote.ApiException;
import pl.temomuko.autostoprace.domain.model.LocationRecord;
import pl.temomuko.autostoprace.domain.model.Team;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.data.remote.TeamNotFoundException;
import pl.temomuko.autostoprace.domain.repository.LocationsRepository;
import pl.temomuko.autostoprace.domain.repository.TeamsRepository;
import pl.temomuko.autostoprace.ui.teamslocationsmap.TeamsLocationsMapMvpView;
import pl.temomuko.autostoprace.ui.teamslocationsmap.TeamsLocationsMapPresenter;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall.WallItemsCreator;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import retrofit2.HttpException;
import retrofit2.Response;
import rx.Observable;
import rx.Single;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rafał Naniewicz on 15.04.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamsLocationsMapPresenterTest {

    private static final int TEST_TEAM_NUMBER = 1;
    @Mock TeamsLocationsMapMvpView mMockTeamsLocationsMapMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    @Mock WallItemsCreator mMockWallItemsCreator;
    @Mock RxCacheHelper<List<Team>> mMockRxAllTeamsCacheHelper;
    @Mock RxCacheHelper<List<LocationRecord>> mMockRxTeamLocationsCacheHelper;
    @Mock TeamsRepository teamsRepository;
    @Mock LocationsRepository locationsRepository;
    private TeamsLocationsMapPresenter mTeamsLocationsMapPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mTeamsLocationsMapPresenter = new TeamsLocationsMapPresenter(
                mMockDataManager,
                teamsRepository,
                mMockErrorHandler,
                mMockWallItemsCreator,
                locationsRepository
        );
        mTeamsLocationsMapPresenter.setupRxCacheHelper(null, mMockRxAllTeamsCacheHelper, mMockRxTeamLocationsCacheHelper);
        when(mMockRxAllTeamsCacheHelper.isCached()).thenReturn(false);
        when(mMockRxTeamLocationsCacheHelper.isCached()).thenReturn(false);
        mTeamsLocationsMapPresenter.attachView(mMockTeamsLocationsMapMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mTeamsLocationsMapPresenter.detachView();
    }

    @Test
    public void testLoadAllTeamsSuccess() {
        //given
        List<Team> teams = new ArrayList<>();
        teams.add(null);
        when(teamsRepository.getAllTeams()).thenReturn(Single.just(teams));
        when(mMockRxAllTeamsCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(teams));

        //when
        mTeamsLocationsMapPresenter.loadAllTeams();

        //then
        verify(mMockTeamsLocationsMapMvpView).setHints(teams);
        verify(mMockTeamsLocationsMapMvpView).setAllTeamsProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setAllTeamsProgress(false);
        verify(mMockTeamsLocationsMapMvpView, never()).showError(any());
        verify(mMockRxAllTeamsCacheHelper).clearCache();
    }

    @Test
    public void testLoadAllTeamsFails() {
        //given
        Response<List<Team>> response = Response.error(HttpStatus.BAD_REQUEST, ResponseBody.create(
                MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), "")
        );
        HttpException exception = new HttpException(response);
        when(teamsRepository.getAllTeams()).thenReturn(Single.error(exception));
        when(mMockRxAllTeamsCacheHelper.getRestoredCachedObservable())
                .thenReturn(Observable.error(new StandardResponseException(response)));

        //when
        mTeamsLocationsMapPresenter.loadAllTeams();

        //then
        verify(mMockTeamsLocationsMapMvpView, never()).setHints(any());
        verify(mMockTeamsLocationsMapMvpView).setAllTeamsProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setAllTeamsProgress(false);
        verify(mMockTeamsLocationsMapMvpView).showError(any());
        verify(mMockRxAllTeamsCacheHelper).clearCache();
    }

    @Test
    public void testLoadTeamLocationsSuccessNotEmpty() {
        //given
        List<LocationRecord> locationRecords = new ArrayList<>();
        locationRecords.add(null);
        when(locationsRepository.getTeamLocations(TEST_TEAM_NUMBER)).thenReturn(Single.just(locationRecords));
        when(mMockRxTeamLocationsCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(locationRecords));

        //when
        mTeamsLocationsMapPresenter.loadTeam(TEST_TEAM_NUMBER);

        //then
        verify(mMockTeamsLocationsMapMvpView).clearCurrentTeamLocations();
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(false);
        verify(mMockTeamsLocationsMapMvpView).setLocationsForMap(locationRecords);
        verify(mMockTeamsLocationsMapMvpView, never()).showNoLocationRecordsInfoForMap();
        verify(mMockTeamsLocationsMapMvpView, never()).showError(any());
        verify(mMockRxTeamLocationsCacheHelper).clearCache();
    }

    @Test
    public void testLoadTeamLocationsSuccessEmpty() {
        //given
        List<LocationRecord> locationRecords = new ArrayList<>();
        when(locationsRepository.getTeamLocations(TEST_TEAM_NUMBER)).thenReturn(Single.just(locationRecords));
        when(mMockRxTeamLocationsCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(locationRecords));

        //when
        mTeamsLocationsMapPresenter.loadTeam(TEST_TEAM_NUMBER);

        //then
        verify(mMockTeamsLocationsMapMvpView).clearCurrentTeamLocations();
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(false);
        verify(mMockTeamsLocationsMapMvpView).setLocationsForMap(locationRecords);
        verify(mMockTeamsLocationsMapMvpView).showNoLocationRecordsInfoForMap();
        verify(mMockTeamsLocationsMapMvpView, never()).showError(any());
        verify(mMockRxTeamLocationsCacheHelper).clearCache();
    }

    @Test
    public void testLoadTeamLocationsFails() {
        //given
        Response<List<LocationRecord>> response = Response.error(HttpStatus.BAD_REQUEST, ResponseBody.create(
                MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), "")
        );
        HttpException exception = new HttpException(response);
        when(locationsRepository.getTeamLocations(TEST_TEAM_NUMBER)).thenReturn(Single.error(exception));
        when(mMockRxTeamLocationsCacheHelper.getRestoredCachedObservable()).thenReturn(
                Observable.error(exception));

        //when
        mTeamsLocationsMapPresenter.loadTeam(TEST_TEAM_NUMBER);

        //then
        verify(mMockTeamsLocationsMapMvpView).clearCurrentTeamLocations();
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(false);
        verify(mMockTeamsLocationsMapMvpView, never()).setLocationsForMap(any());
        verify(mMockTeamsLocationsMapMvpView, never()).showNoLocationRecordsInfoForMap();
        verify(mMockTeamsLocationsMapMvpView).showError(any());
        verify(mMockRxTeamLocationsCacheHelper).clearCache();
    }
}
