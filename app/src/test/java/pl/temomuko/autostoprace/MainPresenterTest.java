package pl.temomuko.autostoprace;

import android.content.pm.PackageManager;

import com.google.android.gms.location.LocationRequest;

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
 * Created by szymen on 2016-01-22.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock MainMvpView mMockMainMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
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
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mMainPresenter.detachView();
    }

    @Test
    public void testLoadLocationsReturnsLocations() throws Exception {
        List<LocationRecord> locationsFromApi = new ArrayList<>();
        locationsFromApi.add(new LocationRecord(12.34, 43.21, ""));
        locationsFromApi.add(new LocationRecord(45.33, 73.51, ""));

        Response<List<LocationRecord>> response = Response.success(locationsFromApi);
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.just(response));

        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new LocationRecord(99.99, 99.99, ""));
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        List<LocationRecord> updatedDatabaseLocationRecords = new ArrayList<>(locationsFromDatabase);
        updatedDatabaseLocationRecords.addAll(locationsFromApi);

        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.just(updatedDatabaseLocationRecords));

        mMainPresenter.loadLocations();
        verify(mMockDataManager).syncWithDatabase(response);
        verify(mMockMainMvpView).setProgress(true);
        verify(mMockMainMvpView).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockMainMvpView).updateLocationRecordsList(updatedDatabaseLocationRecords);
        verify(mMockMainMvpView).startPostService();
        verify(mMockMainMvpView, never()).showEmptyInfo();
        verify(mMockMainMvpView, never()).showError(any(String.class));
        verify(mMockMainMvpView, times(2)).setProgress(false);
    }

    @Test
    public void testLoadLocationsFailsSocketTimeoutException() throws Exception {
        Throwable fakeSocketTimeoutException = new SocketTimeoutException();
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.error(fakeSocketTimeoutException));

        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new LocationRecord(99.99, 99.99, ""));
        when(mMockErrorHandler.getMessage(fakeSocketTimeoutException))
                .thenReturn(FAKE_ERROR_MESSAGE);
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocations();
        verify(mMockMainMvpView).showError(mMockErrorHandler
                .getMessage(fakeSocketTimeoutException));
        verify(mMockMainMvpView).startPostService();
        verify(mMockMainMvpView).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockDataManager, never()).syncWithDatabase(Matchers.<Response<List<LocationRecord>>>any());
        verify(mMockMainMvpView, never()).showEmptyInfo();
    }

    @Test
    public void testLoadLocationsApiReturnsEmptyListWithEmptyDatabase() throws Exception {
        List<LocationRecord> locationRecords = new ArrayList<>();
        Response<List<LocationRecord>> response = Response.success(locationRecords);
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.just(response));

        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocations();
        verify(mMockDataManager).syncWithDatabase(response);
        verify(mMockMainMvpView, times(2)).showEmptyInfo();
        verify(mMockMainMvpView).startPostService();
        verify(mMockMainMvpView, never()).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testLoadLocationsApiFailsWithFilledDatabase() throws Exception {
        Response<List<LocationRecord>> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_ACCEPT_JSON), NOT_FOUND_RESPONSE
                ));
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.just(response));

        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new LocationRecord(99.99, 99.99, ""));
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        StandardResponseException responseException = new StandardResponseException(response);
        when(mMockErrorHandler.getMessage(responseException))
                .thenReturn(FAKE_ERROR_MESSAGE);

        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.error(responseException));

        mMainPresenter.loadLocations();
        verify(mMockMainMvpView).updateLocationRecordsList(locationsFromDatabase);
        verify(mMockDataManager).syncWithDatabase(response);
        verify(mMockMainMvpView).startPostService();
        verify(mMockMainMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockMainMvpView, never()).showEmptyInfo();
    }

    @Test
    public void testLoadLocationsFromApiFailsWithEmptyDatabase() throws Exception {
        Response<List<LocationRecord>> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_ACCEPT_JSON), NOT_FOUND_RESPONSE
                ));
        when(mMockDataManager.getTeamLocationRecordsFromServer())
                .thenReturn(Observable.just(response));

        List<LocationRecord> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.getTeamLocationRecordsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        StandardResponseException responseException = new StandardResponseException(response);
        when(mMockErrorHandler.getMessage(responseException))
                .thenReturn(FAKE_ERROR_MESSAGE);

        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.error(responseException));

        mMainPresenter.loadLocations();
        verify(mMockDataManager).syncWithDatabase(response);
        verify(mMockMainMvpView).showEmptyInfo();
        verify(mMockMainMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockMainMvpView).startPostService();
        verify(mMockMainMvpView, never()).updateLocationRecordsList(locationsFromDatabase);
    }

    @Test
    public void testCheckAuthNotLogged() throws Exception {
        when(mMockDataManager.isLoggedWithToken()).thenReturn(false);
        mMainPresenter.checkAuth();
        verify(mMockMainMvpView).startLauncherActivity();
    }

    @Test
    public void testCheckAuthExpiredSession() throws Exception {
        when(mMockDataManager.isLoggedWithToken()).thenReturn(true);
        Response<SignInResponse> response = Response.error(HttpStatus.UNAUTHORIZED,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_ACCEPT_JSON), UNAUTHORIZED_RESPONSE
                ));
        when(mMockDataManager.validateToken()).thenReturn(Observable.just(response));
        when(mMockDataManager.clearUserData()).thenReturn(Observable.empty());
        mMainPresenter.checkAuth();
        verify(mMockDataManager).clearUserData();
        verify(mMockMainMvpView).showSessionExpiredError();
        verify(mMockMainMvpView).startLoginActivity();
        verify(mMockDataManager, never()).saveAuthorizationResponse(response);
    }

    @Test
    public void testCheckAuthSuccess() throws Exception {
        when(mMockDataManager.isLoggedWithToken()).thenReturn(true);
        SignInResponse signInResponse = new SignInResponse();
        Response<SignInResponse> response = Response.success(signInResponse);
        when(mMockDataManager.validateToken()).thenReturn(Observable.just(response));
        mMainPresenter.checkAuth();
        verify(mMockDataManager).saveAuthorizationResponse(response);
        verify(mMockDataManager, never()).clearUserData();
        verify(mMockMainMvpView, never()).startLoginActivity();
        verify(mMockMainMvpView, never()).startLauncherActivity();
        verify(mMockMainMvpView, never()).showSessionExpiredError();
    }

    @Test
    public void testIsAuthorized() throws Exception {
        when(mMockDataManager.isLoggedWithToken()).thenReturn(true);
        assertTrue(mMainPresenter.isAuthorized());
        when(mMockDataManager.isLoggedWithToken()).thenReturn(false);
        assertFalse(mMainPresenter.isAuthorized());
    }

    @Test
    public void testGoToPostLocationWithPermission() throws Exception {
        when(mMockDataManager.checkLocationSettings(any(LocationRequest.class)))
                .thenReturn(Observable.empty());
        when(mMockDataManager.hasFineLocationPermission()).thenReturn(true);
        mMainPresenter.goToPostLocation();
        verify(mMockMainMvpView, never()).compatRequestFineLocationPermission();
    }

    @Test
    public void testGoToPostLocationWithoutPermission() throws Exception {
        when(mMockPermissionHelper.hasFineLocationPermission()).thenReturn(false);
        mMainPresenter.goToPostLocation();
        verify(mMockMainMvpView).dismissWarning();
        verify(mMockMainMvpView, never()).startPostActivity();
        verify(mMockMainMvpView).compatRequestFineLocationPermission();
    }

    @Test
    public void testHandlePermissionResultGranted() throws Exception {
        when(mMockDataManager.checkLocationSettings(any(LocationRequest.class)))
                .thenReturn(Observable.empty());
        mMainPresenter.handlePermissionResult(FINE_LOCATION_PERMISSION_REQUEST_CODE,
                new int[]{PackageManager.PERMISSION_GRANTED});
        verify(mMockDataManager).checkLocationSettings(any(LocationRequest.class));
        verify(mMockMainMvpView, never()).showNoFineLocationPermissionWarning();
    }

    @Test
    public void testHandlePermissionResultDenied() throws Exception {
        mMainPresenter.handlePermissionResult(FINE_LOCATION_PERMISSION_REQUEST_CODE,
                new int[]{PackageManager.PERMISSION_DENIED});
        verify(mMockMainMvpView, never()).startPostActivity();
        verify(mMockMainMvpView).showNoFineLocationPermissionWarning();
    }
}