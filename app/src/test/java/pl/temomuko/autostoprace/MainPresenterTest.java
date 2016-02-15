package pl.temomuko.autostoprace;

import android.content.pm.PackageManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import pl.temomuko.autostoprace.data.model.Location;
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
import static org.mockito.Matchers.anyInt;
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

    @Ignore //TODO
    @Test
    public void testLoadLocationsReturnsLocations() throws Exception {
        List<Location> locationsFromApi = new ArrayList<>();
        locationsFromApi.add(new Location(12.34, 43.21, ""));
        locationsFromApi.add(new Location(45.33, 73.51, ""));

        Response<List<Location>> response = Response.success(locationsFromApi);
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.just(response));

        List<Location> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new Location(99.99, 99.99, ""));
        when(mMockDataManager.getTeamLocationsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        List<Location> updatedDatabaseLocations = new ArrayList<>(locationsFromDatabase);
        updatedDatabaseLocations.addAll(locationsFromApi);

        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.just(updatedDatabaseLocations));

        mMainPresenter.loadLocations();
        verify(mMockDataManager).syncWithDatabase(response);
        verify(mMockMainMvpView).setProgress(true);
        verify(mMockMainMvpView).updateLocationsList(locationsFromDatabase);
        verify(mMockMainMvpView).updateLocationsList(updatedDatabaseLocations);
        verify(mMockMainMvpView, never()).showEmptyInfo();
        verify(mMockMainMvpView, never()).showError(any(String.class));
        verify(mMockMainMvpView, times(2)).setProgress(false);
    }

    @Test
    public void testLoadLocationsFailsSocketTimeoutException() throws Exception {
        Throwable fakeSocketTimeoutException = new SocketTimeoutException();
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.error(fakeSocketTimeoutException));

        List<Location> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new Location(99.99, 99.99, ""));
        when(mMockErrorHandler.getMessage(fakeSocketTimeoutException))
                .thenReturn(FAKE_ERROR_MESSAGE);
        when(mMockDataManager.getTeamLocationsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocations();
        verify(mMockMainMvpView).showError(mMockErrorHandler
                .getMessage(fakeSocketTimeoutException));
        verify(mMockMainMvpView).updateLocationsList(locationsFromDatabase);
        verify(mMockDataManager, never()).syncWithDatabase(Matchers.<Response<List<Location>>>any());
        verify(mMockMainMvpView, never()).showEmptyInfo();
    }

    @Ignore //TODO
    @Test
    public void testLoadLocationsApiReturnsEmptyListWithEmptyDatabase() throws Exception {
        List<Location> locations = new ArrayList<>();
        Response<List<Location>> response = Response.success(locations);
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.just(response));

        List<Location> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.getTeamLocationsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocations();
        verify(mMockDataManager).syncWithDatabase(response);
        verify(mMockMainMvpView, times(2)).showEmptyInfo();
        verify(mMockMainMvpView, never()).updateLocationsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testLoadLocationsApiFailsWithFilledDatabase() throws Exception {
        Response<List<Location>> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_ACCEPT_JSON), NOT_FOUND_RESPONSE
                ));
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.just(response));

        List<Location> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new Location(99.99, 99.99, ""));
        when(mMockDataManager.getTeamLocationsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        StandardResponseException responseException = new StandardResponseException(response);
        when(mMockErrorHandler.getMessage(responseException))
                .thenReturn(FAKE_ERROR_MESSAGE);

        when(mMockDataManager.syncWithDatabase(response))
                .thenReturn(Observable.error(responseException));

        mMainPresenter.loadLocations();
        verify(mMockMainMvpView).updateLocationsList(locationsFromDatabase);
        verify(mMockDataManager).syncWithDatabase(response);
        verify(mMockMainMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockMainMvpView, never()).showEmptyInfo();
    }

    @Test
    public void testLoadLocationsFromApiFailsWithEmptyDatabase() throws Exception {
        Response<List<Location>> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_ACCEPT_JSON), NOT_FOUND_RESPONSE
                ));
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.just(response));

        List<Location> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.getTeamLocationsFromDatabase())
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
        verify(mMockMainMvpView, never()).updateLocationsList(locationsFromDatabase);
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
        when(mMockMainMvpView.hasLocationPermission()).thenReturn(true);
        mMainPresenter.goToPostLocation();
        verify(mMockMainMvpView).dismissNoFineLocationPermissionSnackbar();
        verify(mMockMainMvpView).startPostActivity();
        verify(mMockMainMvpView, never()).compatRequestFineLocationPermission(anyInt());
    }

    @Test
    public void testGoToPostLocationWithoutPermission() throws Exception {
        when(mMockMainMvpView.hasLocationPermission()).thenReturn(false);
        mMainPresenter.goToPostLocation();
        verify(mMockMainMvpView).dismissNoFineLocationPermissionSnackbar();
        verify(mMockMainMvpView, never()).startPostActivity();
        verify(mMockMainMvpView).compatRequestFineLocationPermission(anyInt());
    }

    @Test
    public void testHandlePermissionResultGranted() throws Exception {
        mMainPresenter.handlePermissionResult(FINE_LOCATION_PERMISSION_REQUEST_CODE,
                new int[]{PackageManager.PERMISSION_GRANTED});
        verify(mMockMainMvpView).startPostActivity();
        verify(mMockMainMvpView,never()).showNoFineLocationPermissionSnackbar();
    }

    @Test
    public void testHandlePermissionResultDenied() throws Exception {
        mMainPresenter.handlePermissionResult(FINE_LOCATION_PERMISSION_REQUEST_CODE,
                new int[]{PackageManager.PERMISSION_DENIED});
        verify(mMockMainMvpView,never()).startPostActivity();
        verify(mMockMainMvpView).showNoFineLocationPermissionSnackbar();
    }
}