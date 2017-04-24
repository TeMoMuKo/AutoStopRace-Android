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
import pl.temomuko.autostoprace.data.local.PermissionHelper;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.main.MainMvpView;
import pl.temomuko.autostoprace.ui.main.MainPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import retrofit2.Response;
import rx.Completable;
import rx.Observable;
import rx.Single;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    private static final String FAKE_ERROR_MESSAGE = "fake_error_message";
    private static final String NOT_FOUND_RESPONSE =
            "{ \"status\": 404, \"error\": \"Not Found\" }}";
    private static final String UNAUTHORIZED_RESPONSE =
            "{ \"errors\": [ \"Invalid login credentials. Please try again.\" ] }";

    @Mock MainMvpView mMockMainMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    @Mock PermissionHelper mMockPermissionHelper;
    private MainPresenter mMainPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mMainPresenter = new MainPresenter(mMockDataManager, mMockErrorHandler);
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mMainPresenter.detachView();
    }

    @Test
    public void testLoadLocationsReturnsLocations() throws Exception {
        //given
        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(TestConstants.PROPER_LOCATION_RECORD);
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Single.just(locationsFromDatabase));
        //when
        mMainPresenter.loadLocations();

        //then
        verify(mMockMainMvpView).setProgress(true);
        verify(mMockMainMvpView).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showEmptyInfo();
        verify(mMockMainMvpView, never()).showError(any(String.class));
        verify(mMockMainMvpView).setProgress(false);
    }

    @Test
    public void testLoadLocationsWithEmptyDatabase() throws Exception {
        //given
        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Single.just(locationsFromDatabase));

        //when
        mMainPresenter.loadLocations();

        //then
        verify(mMockMainMvpView).setProgress(true);
        verify(mMockMainMvpView).showEmptyInfo();
        verify(mMockMainMvpView, never()).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showError(any(String.class));
        verify(mMockMainMvpView).setProgress(false);
    }

    @Test
    public void testCheckAuthNotLogged() throws Exception {
        //given
        when(mMockDataManager.isLoggedWithToken()).thenReturn(false);

        //when
        mMainPresenter.checkAuth();

        //then
        verify(mMockMainMvpView).startLauncherActivity();
    }

    @Test
    public void testCheckAuthExpiredSession() throws Exception {
        //given
        when(mMockDataManager.isLoggedWithToken()).thenReturn(true);
        Response<SignInResponse> response = Response.error(HttpStatus.UNAUTHORIZED,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), UNAUTHORIZED_RESPONSE
                ));
        when(mMockDataManager.validateToken()).thenReturn(Observable.just(response));
        when(mMockDataManager.clearUserData()).thenReturn(Completable.complete());

        //when
        mMainPresenter.checkAuth();

        //then
        verify(mMockDataManager).clearUserData();
        verify(mMockMainMvpView).showSessionExpiredError();
        verify(mMockMainMvpView).disablePostLocationShortcut();
        verify(mMockMainMvpView).startLoginActivity();
        verify(mMockDataManager, never()).saveAuthorizationResponse(response);
    }

    @Test
    public void testCheckAuthSuccess() throws Exception {
        //given
        when(mMockDataManager.isLoggedWithToken()).thenReturn(true);
        SignInResponse signInResponse = new SignInResponse();
        Response<SignInResponse> response = Response.success(signInResponse);
        when(mMockDataManager.validateToken()).thenReturn(Observable.just(response));

        //when
        mMainPresenter.checkAuth();

        //then
        verify(mMockDataManager).saveAuthorizationResponse(response);
        verify(mMockDataManager, never()).clearUserData();
        verify(mMockMainMvpView, never()).startLoginActivity();
        verify(mMockMainMvpView, never()).startLauncherActivity();
        verify(mMockMainMvpView, never()).showSessionExpiredError();
        verify(mMockMainMvpView, never()).disablePostLocationShortcut();
    }

    @Test
    public void testIsAuthorized() throws Exception {
        //given
        when(mMockDataManager.isLoggedWithToken()).thenReturn(true);

        //assert
        assertTrue(mMainPresenter.isAuthorized());

        //given
        when(mMockDataManager.isLoggedWithToken()).thenReturn(false);

        //assert
        assertFalse(mMainPresenter.isAuthorized());
    }

    @Test
    public void testGoToPostLocationWithPermission() throws Exception {
        //given
        when(mMockDataManager.checkLocationSettings())
                .thenReturn(Observable.empty());
        when(mMockDataManager.hasFineLocationPermission()).thenReturn(true);

        //when
        mMainPresenter.goToPostLocation();

        //then
        verify(mMockMainMvpView, never()).compatRequestFineLocationPermission();
    }

    @Test
    public void testGoToPostLocationWithoutPermission() throws Exception {
        //given
        when(mMockPermissionHelper.hasFineLocationPermission()).thenReturn(false);

        //when
        mMainPresenter.goToPostLocation();

        //then
        verify(mMockMainMvpView).dismissWarning();
        verify(mMockMainMvpView, never()).startPostActivity();
        verify(mMockMainMvpView).compatRequestFineLocationPermission();
    }

    @Test
    public void testHandlePermissionResultGranted() throws Exception {
        //given
        when(mMockDataManager.checkLocationSettings())
                .thenReturn(Observable.empty());

        //when
        mMainPresenter.handleFineLocationRequestPermissionResult(true);

        //then
        verify(mMockDataManager).checkLocationSettings();
        verify(mMockMainMvpView, never()).showNoFineLocationPermissionWarning();
    }

    @Test
    public void testHandlePermissionResultDenied() throws Exception {
        //when
        mMainPresenter.handleFineLocationRequestPermissionResult(false);

        //then
        verify(mMockMainMvpView, never()).startPostActivity();
        verify(mMockMainMvpView).showNoFineLocationPermissionWarning();
    }

    @Test
    public void testSyncLocationsIfRecentlyNotSyncedOneHourPassed() throws Exception {
        //given
        when(mMockDataManager.getLastLocationSyncTimestamp()).thenReturn((long) 0);

        //when
        mMainPresenter.syncLocationsIfRecentlyNotSynced();

        //then
        verify(mMockMainMvpView).startLocationSyncService();
    }

    @Test
    public void testSyncLocationsIfRecentlyNotSyncedRecentlySynced() throws Exception {
        //given
        when(mMockDataManager.getLastLocationSyncTimestamp()).thenReturn(System.currentTimeMillis());

        //when
        mMainPresenter.syncLocationsIfRecentlyNotSynced();

        //then
        verify(mMockMainMvpView, never()).startLocationSyncService();
    }

    @Test
    public void testGoToPhrasebook() {
        //when
        mMainPresenter.goToPhrasebook();

        //then
        verify(mMockMainMvpView).startPhrasebookActivity();
    }
}