package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import pl.temomuko.autostoprace.ui.main.MainMvpView;
import pl.temomuko.autostoprace.ui.main.MainPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatus;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import retrofit2.Response;
import rx.Observable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
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
    public void testLoadLocationsFromApiReturnsLocations() throws Exception {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(12.34, 43.21, ""));
        Response<List<Location>> response = Response.success(locations);
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.just(response));

        List<Location> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new Location(99.99, 99.99, ""));
        when(mMockDataManager.saveAndEmitLocationsFromDatabase(locations))
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocationsFromServer();
        verify(mMockDataManager).saveAndEmitLocationsFromDatabase(locations);
        verify(mMockMainMvpView).updateLocationsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showEmptyInfo();
        verify(mMockMainMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testLoadLocationsFromApiFailsSocketTimeoutException() throws Exception {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(12.34, 43.21, ""));
        Throwable fakeException = new SocketTimeoutException();
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.error(fakeException));

        List<Location> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new Location(99.99, 99.99, ""));
        when(mMockErrorHandler.getMessageFromRetrofitThrowable(fakeException))
                .thenReturn(FAKE_ERROR_MESSAGE);
        when(mMockDataManager.getTeamLocationsFromDatabase())
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocationsFromServer();
        verify(mMockMainMvpView).showError(mMockErrorHandler
                .getMessageFromRetrofitThrowable(fakeException));
        verify(mMockDataManager, never()).saveAndEmitLocationsFromDatabase(anyListOf(Location.class));
        verify(mMockMainMvpView, never()).showEmptyInfo();
    }

    @Test
    public void testLoadLocationsFromApiReturnsEmptyList() throws Exception {
        List<Location> locations = new ArrayList<>();
        Response<List<Location>> response = Response.success(locations);
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.just(response));

        List<Location> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.saveAndEmitLocationsFromDatabase(locations))
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocationsFromServer();
        verify(mMockDataManager).saveAndEmitLocationsFromDatabase(locations);
        verify(mMockMainMvpView).showEmptyInfo();
        verify(mMockMainMvpView, never()).updateLocationsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testLoadLocationsFromApiFailsWithFilledDatabase() throws Exception {
        List<Location> locations = new ArrayList<>();
        Response<List<Location>> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_ACCEPT_JSON), NOT_FOUND_RESPONSE
                ));
        when(mMockDataManager.getTeamLocationsFromServer()).thenReturn(Observable.just(response));
        List<Location> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new Location(99.99, 99.99, ""));
        when(mMockDataManager.saveAndEmitLocationsFromDatabase(locations))
                .thenReturn(Observable.just(locationsFromDatabase));
        when(mMockDataManager.getTeamLocationsFromDatabase()).thenReturn(Observable.just(locationsFromDatabase));
        when(mMockErrorHandler.getMessageFromResponse(response)).thenReturn(FAKE_ERROR_MESSAGE);

        mMainPresenter.loadLocationsFromServer();
        verify(mMockMainMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockDataManager).getTeamLocationsFromDatabase();
        verify(mMockMainMvpView).updateLocationsList(locationsFromDatabase);
        verify(mMockDataManager, never()).saveAndEmitLocationsFromDatabase(locations);
        verify(mMockMainMvpView, never()).showEmptyInfo();
    }

    @Test
    public void testLoadLocationsFromApiFailsWithEmptyDatabase() throws Exception {
        List<Location> locations = new ArrayList<>();
        Response<List<Location>> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_ACCEPT_JSON), NOT_FOUND_RESPONSE
                ));
        when(mMockDataManager.getTeamLocationsFromServer()).thenReturn(Observable.just(response));
        List<Location> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.saveAndEmitLocationsFromDatabase(locations))
                .thenReturn(Observable.just(locationsFromDatabase));
        when(mMockDataManager.getTeamLocationsFromDatabase()).thenReturn(Observable.just(locationsFromDatabase));
        when(mMockErrorHandler.getMessageFromResponse(response)).thenReturn(FAKE_ERROR_MESSAGE);

        mMainPresenter.loadLocationsFromServer();
        verify(mMockMainMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockDataManager).getTeamLocationsFromDatabase();
        verify(mMockMainMvpView,never()).updateLocationsList(locationsFromDatabase);
        verify(mMockDataManager, never()).saveAndEmitLocationsFromDatabase(locations);
        verify(mMockMainMvpView).showEmptyInfo();
    }

    @Test
    public void testLoadLocationsFromDatabaseReturnEmptyList() {
        List<Location> locations = new ArrayList<>();
        when(mMockDataManager.getTeamLocationsFromDatabase()).thenReturn(Observable.just(locations));
        mMainPresenter.loadLocationsFromDatabase();
        verify(mMockMainMvpView).showEmptyInfo();
        verify(mMockMainMvpView, never()).updateLocationsList(locations);
    }

    @Test
    public void testLoadLocationsFromDatabaseReturnLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(1.09, 17.09, ""));
        when(mMockDataManager.getTeamLocationsFromDatabase()).thenReturn(Observable.just(locations));
        mMainPresenter.loadLocationsFromDatabase();
        verify(mMockMainMvpView, never()).showEmptyInfo();
        verify(mMockMainMvpView).updateLocationsList(locations);
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
    public void testGoToPostLocation() throws Exception {
        mMainPresenter.goToPostLocation();
        verify(mMockMainMvpView).startPostActivity();
    }
}