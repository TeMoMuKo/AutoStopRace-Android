package pl.temomuko.autostoprace;

import android.content.pm.PackageManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.local.PermissionHelper;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.ui.main.MainMvpView;
import pl.temomuko.autostoprace.ui.main.MainPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import retrofit2.Response;
import rx.Observable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock MainMvpView mMockMainMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    @Mock RxCacheHelper<List<LocationRecord>> mMockRxCacheHelper;
    @Mock PermissionHelper mMockPermissionHelper;
    private MainPresenter mMainPresenter;
    private static final String FAKE_ERROR_MESSAGE = "fake_error_message";
    private static final String NOT_FOUND_RESPONSE =
            "{ \"status\": 404, \"error\": \"Not Found\" }}";
    private static final String UNAUTHORIZED_RESPONSE =
            "{ \"errors\": [ \"Invalid login credentials. Please try again.\" ] }";
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mMainPresenter = new MainPresenter(mMockDataManager, mMockErrorHandler);
        mMainPresenter.setupRxCacheHelper(null, mMockRxCacheHelper);
        when(mMockRxCacheHelper.isCached()).thenReturn(false);
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mMainPresenter.detachView();
    }

    @Test
    public void testLoadLocationsReturnsLocations() throws Exception {
        //given
        List<LocationRecord> locationsFromApi = new ArrayList<>();
        locationsFromApi.add(new LocationRecord(12.34, 43.21, "Yo", "Somewhere, Poland", "Poland", "PL"));
        locationsFromApi.add(new LocationRecord(45.33, 73.51, "Yo", "Somewhere, Poland", "Poland", "PL"));
        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new LocationRecord(99.99, 99.99, "Yo", "Somewhere, Poland", "Poland", "PL"));
        locationsFromApi.addAll(locationsFromDatabase);
        Response<List<LocationRecord>> response = Response.success(locationsFromApi);
        when(mMockRxCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(locationsFromApi));
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.just(response));
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        //when
        mMainPresenter.loadLocations();

        //then
        verify(mMockMainMvpView).setProgress(true);
        verify(mMockMainMvpView).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockMainMvpView).updateLocationRecordsList(locationsFromApi);
        verify(mMockMainMvpView, never()).showEmptyInfo();
        verify(mMockMainMvpView, never()).showError(any(String.class));
        verify(mMockMainMvpView, times(2)).setProgress(false);
    }

    @Test
    public void testLoadLocationsFailsSocketTimeoutException() throws Exception {
        //given
        Throwable fakeSocketTimeoutException = new SocketTimeoutException();
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.error(fakeSocketTimeoutException));
        when(mMockRxCacheHelper.getRestoredCachedObservable())
                .thenReturn(Observable.error(fakeSocketTimeoutException));
        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new LocationRecord(99.99, 99.99, "Yo", "Somewhere, Poland", "Poland", "PL"));
        when(mMockErrorHandler.getMessage(fakeSocketTimeoutException))
                .thenReturn(FAKE_ERROR_MESSAGE);
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        //when
        mMainPresenter.loadLocations();

        //then
        verify(mMockMainMvpView).showError(mMockErrorHandler
                .getMessage(fakeSocketTimeoutException));
        verify(mMockMainMvpView).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockDataManager, never()).syncWithDatabase(Matchers.<Response<List<LocationRecord>>>any());
        verify(mMockMainMvpView, never()).showEmptyInfo();
    }

    @Test
    public void testLoadLocationsApiReturnsEmptyListWithEmptyDatabase() throws Exception {
        //given
        List<LocationRecord> locationRecords = new ArrayList<>();
        Response<List<LocationRecord>> response = Response.success(locationRecords);
        when(mMockRxCacheHelper.getRestoredCachedObservable())
                .thenReturn(Observable.just(response.body()));
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.just(response));
        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));
        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.just(locationsFromDatabase));

        //when
        mMainPresenter.loadLocations();

        //then
        verify(mMockMainMvpView, times(2)).showEmptyInfo();
        verify(mMockMainMvpView, never()).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testLoadLocationsApiFailsWithFilledDatabase() throws Exception {
        //given
        Response<List<LocationRecord>> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), NOT_FOUND_RESPONSE
                ));
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.just(response));
        StandardResponseException responseException = new StandardResponseException(response);
        when(mMockRxCacheHelper.getRestoredCachedObservable())
                .thenReturn(Observable.error(responseException));
        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new LocationRecord(99.99, 99.99, "Yo", "Somewhere, Poland", "Poland", "PL"));
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));
        when(mMockErrorHandler.getMessage(responseException))
                .thenReturn(FAKE_ERROR_MESSAGE);

        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.error(responseException));

        //when
        mMainPresenter.loadLocations();

        //then
        verify(mMockMainMvpView).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockMainMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockMainMvpView, never()).showEmptyInfo();
    }

    @Test
    public void testLoadLocationsFromApiFailsWithEmptyDatabase() throws Exception {
        //given
        Response<List<LocationRecord>> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), NOT_FOUND_RESPONSE
                ));
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.just(response));

        StandardResponseException responseException = new StandardResponseException(response);
        when(mMockRxCacheHelper.getRestoredCachedObservable())
                .thenReturn(Observable.error(responseException));

        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));
        when(mMockErrorHandler.getMessage(responseException))
                .thenReturn(FAKE_ERROR_MESSAGE);

        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.error(responseException));

        //when
        mMainPresenter.loadLocations();

        //then
        verify(mMockMainMvpView).showEmptyInfo();
        verify(mMockMainMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockMainMvpView, never()).updateLocationRecordsList(locationsFromDatabase);
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
        when(mMockDataManager.clearUserData()).thenReturn(Observable.empty());

        //when
        mMainPresenter.checkAuth();

        //then
        verify(mMockDataManager).clearUserData();
        verify(mMockMainMvpView).showSessionExpiredError();
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
        mMainPresenter.handlePermissionResult(FINE_LOCATION_PERMISSION_REQUEST_CODE,
                new int[]{PackageManager.PERMISSION_GRANTED});

        //then
        verify(mMockDataManager).checkLocationSettings();
        verify(mMockMainMvpView, never()).showNoFineLocationPermissionWarning();
    }

    @Test
    public void testHandlePermissionResultDenied() throws Exception {
        //when
        mMainPresenter.handlePermissionResult(FINE_LOCATION_PERMISSION_REQUEST_CODE,
                new int[]{PackageManager.PERMISSION_DENIED});

        //then
        verify(mMockMainMvpView, never()).startPostActivity();
        verify(mMockMainMvpView).showNoFineLocationPermissionWarning();
    }
}