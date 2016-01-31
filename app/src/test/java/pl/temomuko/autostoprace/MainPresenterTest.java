package pl.temomuko.autostoprace;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.ui.main.MainMvpView;
import pl.temomuko.autostoprace.ui.main.MainPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatus;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import retrofit.Response;
import rx.Observable;

import static org.mockito.Matchers.any;
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
    private static final String FAKE_FIRST_NAME = "fake_first_name";
    private static final String FAKE_LAST_NAME = "fake_last_name";
    private static final String FAKE_EMAIL = "fake_email";
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
        when(mMockDataManager.saveLocationsToDatabase(locations))
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocationsFromServer();
        verify(mMockDataManager).saveLocationsToDatabase(locations);
        verify(mMockMainMvpView).updateLocationsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showEmptyInfo();
        verify(mMockMainMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testLoadLocationsFromApiReturnsEmptyList() throws Exception {
        List<Location> locations = new ArrayList<>();
        Response<List<Location>> response = Response.success(locations);
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.just(response));

        List<Location> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.saveLocationsToDatabase(locations))
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocationsFromServer();
        verify(mMockDataManager).saveLocationsToDatabase(locations);
        verify(mMockMainMvpView).showEmptyInfo();
        verify(mMockMainMvpView, never()).updateLocationsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testLoadLocationsFromApiFails() throws Exception {
        List<Location> locations = new ArrayList<>();
        Response<List<Location>> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_ACCEPT_JSON), NOT_FOUND_RESPONSE
                ));
        when(mMockDataManager.getTeamLocationsFromServer()).thenReturn(Observable.just(response));
        List<Location> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new Location(99.99, 99.99, ""));
        when(mMockDataManager.saveLocationsToDatabase(locations))
                .thenReturn(Observable.just(locationsFromDatabase));
        when(mMockErrorHandler.getMessage(response)).thenReturn(FAKE_ERROR_MESSAGE);

        mMainPresenter.loadLocationsFromServer();
        verify(mMockMainMvpView).showError(mMockErrorHandler.getMessage(response));
        verify(mMockMainMvpView, never()).updateLocationsList(locationsFromDatabase);
        verify(mMockDataManager, never()).saveLocationsToDatabase(locations);
        verify(mMockMainMvpView, never()).showEmptyInfo();
    }

    @Test
    public void testLoadLocationsFromDatabase() {
        //TODO
        mMainPresenter.loadLocationsFromDatabase();
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
                        MediaType.parse("application/json"), UNAUTHORIZED_RESPONSE
                ));
        when(mMockDataManager.validateToken()).thenReturn(Observable.just(response));
        mMainPresenter.checkAuth();
        verify(mMockDataManager).clearAuth();
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
        verify(mMockDataManager, never()).clearAuth();
        verify(mMockMainMvpView, never()).startLoginActivity();
        verify(mMockMainMvpView, never()).startLauncherActivity();
        verify(mMockMainMvpView, never()).showSessionExpiredError();

    }

    @Test
    public void testSetupUserInfo() throws Exception {
        User fakeUser = new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL);
        when(mMockDataManager.getCurrentUser()).thenReturn(fakeUser);
        mMainPresenter.setupUserInfo();
        verify(mMockMainMvpView).showUser(fakeUser);
    }

    @Test
    public void testLogout() throws Exception {
        when(mMockDataManager.signOut()).thenReturn(Observable.<Response<SignOutResponse>>empty());
        mMainPresenter.logout();
        verify(mMockDataManager).clearAuth();
        verify(mMockMainMvpView).showLogoutMessage();
        verify(mMockMainMvpView).startLauncherActivity();
    }

    @Test
    public void testGoToPostLocation() throws Exception {
        mMainPresenter.goToPostLocation();
        verify(mMockMainMvpView).startPostActivity();
    }
}