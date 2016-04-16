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
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.data.remote.TeamNotFoundException;
import pl.temomuko.autostoprace.ui.teamslocationsmap.TeamsLocationsMapMvpView;
import pl.temomuko.autostoprace.ui.teamslocationsmap.TeamsLocationsMapPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import retrofit2.Response;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
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
    @Mock RxCacheHelper<Response<List<Team>>> mMockRxAllTeamsCacheHelper;
    @Mock RxCacheHelper<Response<List<LocationRecord>>> mMockRxTeamLocationsCacheHelper;

    private TeamsLocationsMapPresenter mTeamsLocationsMapPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mTeamsLocationsMapPresenter = new TeamsLocationsMapPresenter(mMockDataManager, mMockErrorHandler);
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
        Response<List<Team>> response = Response.success(teams);
        when(mMockDataManager.getAllTeams()).thenReturn(Observable.just(response));
        when(mMockRxAllTeamsCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(response));
        //when
        mTeamsLocationsMapPresenter.loadAllTeams();
        //then
        verify(mMockDataManager, only()).getAllTeams();
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
        when(mMockDataManager.getAllTeams()).thenReturn(Observable.just(response));
        when(mMockRxAllTeamsCacheHelper.getRestoredCachedObservable())
                .thenReturn(Observable.error(new StandardResponseException(response)));
        //when
        mTeamsLocationsMapPresenter.loadAllTeams();
        //then
        verify(mMockDataManager, only()).getAllTeams();
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
        Response<List<LocationRecord>> response = Response.success(locationRecords);
        when(mMockDataManager.getTeamLocationRecordsFromServer(TEST_TEAM_NUMBER)).thenReturn(Observable.just(response));
        when(mMockRxTeamLocationsCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(response));
        //when
        mTeamsLocationsMapPresenter.loadTeam(TEST_TEAM_NUMBER);
        //then
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(false);
        verify(mMockTeamsLocationsMapMvpView).setLocations(locationRecords);
        verify(mMockTeamsLocationsMapMvpView, never()).showNoLocationRecordsInfo();
        verify(mMockTeamsLocationsMapMvpView, never()).showError(any());
        verify(mMockRxTeamLocationsCacheHelper).clearCache();
    }

    @Test
    public void testLoadTeamLocationsSuccessEmpty() {
        //given
        List<LocationRecord> locationRecords = new ArrayList<>();
        Response<List<LocationRecord>> response = Response.success(locationRecords);
        when(mMockDataManager.getTeamLocationRecordsFromServer(TEST_TEAM_NUMBER)).thenReturn(Observable.just(response));
        when(mMockRxTeamLocationsCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(response));
        //when
        mTeamsLocationsMapPresenter.loadTeam(TEST_TEAM_NUMBER);
        //then
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(false);
        verify(mMockTeamsLocationsMapMvpView).setLocations(locationRecords);
        verify(mMockTeamsLocationsMapMvpView).showNoLocationRecordsInfo();
        verify(mMockTeamsLocationsMapMvpView, never()).showError(any());
        verify(mMockRxTeamLocationsCacheHelper).clearCache();
    }

    @Test
    public void testLoadTeamLocationsFails() {
        //given
        Response<List<LocationRecord>> response = Response.error(HttpStatus.BAD_REQUEST, ResponseBody.create(
                MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), "")
        );
        when(mMockDataManager.getTeamLocationRecordsFromServer(TEST_TEAM_NUMBER)).thenReturn(Observable.just(response));
        when(mMockRxTeamLocationsCacheHelper.getRestoredCachedObservable()).thenReturn(
                Observable.error(new StandardResponseException(response)));
        //when
        mTeamsLocationsMapPresenter.loadTeam(TEST_TEAM_NUMBER);
        //then
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(false);
        verify(mMockTeamsLocationsMapMvpView, never()).setLocations(any());
        verify(mMockTeamsLocationsMapMvpView, never()).showNoLocationRecordsInfo();
        verify(mMockTeamsLocationsMapMvpView).showError(any());
        verify(mMockRxTeamLocationsCacheHelper).clearCache();
    }

    @Test
    public void testLoadTeamLocationsTeamNotFound() {
        //given
        final String teamNotFound = "team not found";
        Response<List<LocationRecord>> response = Response.error(HttpStatus.NOT_FOUND, ResponseBody.create(
                MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), "")
        );
        when(mMockDataManager.getTeamLocationRecordsFromServer(TEST_TEAM_NUMBER)).thenReturn(Observable.just(response));
        when(mMockErrorHandler.getMessage(any())).thenReturn(teamNotFound);
        when(mMockRxTeamLocationsCacheHelper.getRestoredCachedObservable()).thenReturn(
                Observable.error(new TeamNotFoundException(response)));
        //when
        mTeamsLocationsMapPresenter.loadTeam(TEST_TEAM_NUMBER);
        //then
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setTeamProgress(false);
        verify(mMockTeamsLocationsMapMvpView, never()).setLocations(any());
        verify(mMockTeamsLocationsMapMvpView, never()).showNoLocationRecordsInfo();
        verify(mMockTeamsLocationsMapMvpView).showError(teamNotFound);
        verify(mMockRxTeamLocationsCacheHelper).clearCache();
    }
}
