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
import pl.temomuko.autostoprace.ui.teamslocationsmap.TeamsLocationsMapMvpView;
import pl.temomuko.autostoprace.ui.teamslocationsmap.TeamsLocationsMapPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import retrofit2.Response;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rafał Naniewicz on 15.04.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamsLocationsMapPresenterTest {

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
        verify(mMockDataManager).getAllTeams();
        verify(mMockTeamsLocationsMapMvpView).setHints(teams);
        verify(mMockTeamsLocationsMapMvpView).setAllTeamsProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setAllTeamsProgress(false);
        verify(mMockTeamsLocationsMapMvpView, never()).showError(any());
    }

    @Test
    public void testLoadAllTeamsFails() {
        //given
        List<Team> teams = new ArrayList<>();
        Response<List<Team>> response = Response.error(HttpStatus.BAD_REQUEST, ResponseBody.create(
                MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), "")
        );
        when(mMockDataManager.getAllTeams()).thenReturn(Observable.just(response));
        when(mMockRxAllTeamsCacheHelper.getRestoredCachedObservable())
                .thenReturn(Observable.error(new StandardResponseException(response)));
        //when
        mTeamsLocationsMapPresenter.loadAllTeams();
        //then
        verify(mMockDataManager).getAllTeams();
        verify(mMockTeamsLocationsMapMvpView, never()).setHints(teams);
        verify(mMockTeamsLocationsMapMvpView).setAllTeamsProgress(true);
        verify(mMockTeamsLocationsMapMvpView).setAllTeamsProgress(false);
        verify(mMockTeamsLocationsMapMvpView).showError(any());
    }
}
