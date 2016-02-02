package pl.temomuko.autostoprace.data;

import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.model.CreateLocationRequest;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.AsrService;
import retrofit2.Response;
import rx.Observable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by szymen on 2016-01-27.
 */

@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {

    @Mock PrefsHelper mMockPrefsHelper;
    @Mock AsrService mMockAsrService;
    private DataManager mDataManager;
    private static String FAKE_EMAIL = "fake_email";
    private static String FAKE_PASS = "fake_pass";
    private static String FAKE_ACCESS_TOKEN = "fake_access_token";
    private static String FAKE_CLIENT = "fake_client";
    private static String FAKE_UID = "fake_uid";
    private static String FAKE_FIRST_NAME = "fake_first_name";
    private static String FAKE_LAST_NAME = "fake_last_name";

    private okhttp3.Response.Builder mOkHttpResponseBuilder;

    @Before
    public void setUp() throws Exception {
        mDataManager = new DataManager(mMockAsrService, mMockPrefsHelper);
        setupFakeResponseBuilder();
    }

    private void setupFakeResponseBuilder() {
        HttpUrl httpUrl = new HttpUrl.Builder().scheme("https").host("api.autostoprace.pl").build();
        Request request = new Request.Builder().url(httpUrl).build();
        mOkHttpResponseBuilder = new okhttp3.Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200);
    }

    @Test
    public void testGetTeamLocationsFromServer() throws Exception {
        List<Location> locations = new ArrayList<>();
        Response<List<Location>> response = Response.success(locations);
        when(mMockPrefsHelper.getCurrentUser()).thenReturn(new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL));
        when(mMockAsrService.getLocationsWithObservable(1)).thenReturn(Observable.just(response));
        Observable<Response<List<Location>>> expectedObservable =
                mMockAsrService.getLocationsWithObservable(mMockPrefsHelper.getCurrentUser().getTeamId());
        Observable<Response<List<Location>>> actualObservable =
                mDataManager.getTeamLocationsFromServer();
        assertEquals(expectedObservable, actualObservable);
    }

    @Test
    public void testValidateToken() throws Exception {
        SignInResponse signInResponse = new SignInResponse();
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);
        when(mMockPrefsHelper.getAuthClient()).thenReturn(FAKE_CLIENT);
        when(mMockPrefsHelper.getAuthUid()).thenReturn(FAKE_UID);
        when(mMockAsrService.validateTokenWithObservable(FAKE_ACCESS_TOKEN, FAKE_CLIENT, FAKE_UID))
                .thenReturn(Observable.just(Response.success(signInResponse)));
        mDataManager.validateToken();
        verify(mMockAsrService).validateTokenWithObservable(FAKE_ACCESS_TOKEN, FAKE_CLIENT, FAKE_UID);
    }

    @Test
    public void testClearAuth() throws Exception {
        mDataManager.clearAuth();
        verify(mMockPrefsHelper).clearAuth();
    }

    @Test
    public void testSaveLocationsToDatabase() throws Exception {
        //TODO
    }

    @Test
    public void testSaveLocationToDatabase() throws Exception {
        //TODO
    }

    @Test
    public void testPostLocationToServer() throws Exception {
        Location locationToSend = new Location(12.34, 56.78, "");
        CreateLocationRequest request = new CreateLocationRequest(locationToSend);
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);
        when(mMockPrefsHelper.getAuthClient()).thenReturn(FAKE_CLIENT);
        when(mMockPrefsHelper.getAuthUid()).thenReturn(FAKE_UID);
        when(mMockAsrService.postLocationWithObservable(
                FAKE_ACCESS_TOKEN, FAKE_CLIENT, FAKE_UID, request))
                .thenReturn(Observable.just(Response.success(locationToSend)));
        mDataManager.postLocationToServer(request);
        verify(mMockAsrService).postLocationWithObservable(
                FAKE_ACCESS_TOKEN, FAKE_CLIENT, FAKE_UID, request);
    }

    @Test
    public void testSignIn() throws Exception {
        when(mMockAsrService.signInWithObservable(FAKE_EMAIL, FAKE_PASS))
                .thenReturn(Observable.<Response<SignInResponse>>empty());
        Observable<Response<SignInResponse>> expectedObservable
                = mMockAsrService.signInWithObservable(FAKE_EMAIL, FAKE_PASS);
        Observable<Response<SignInResponse>> actualObservable = mDataManager.signIn(FAKE_EMAIL, FAKE_PASS);
        assertEquals(expectedObservable, actualObservable);
    }

    @Test
    public void testSignOut() throws Exception {
        SignOutResponse signOutResponse = new SignOutResponse();
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);
        when(mMockPrefsHelper.getAuthClient()).thenReturn(FAKE_CLIENT);
        when(mMockPrefsHelper.getAuthUid()).thenReturn(FAKE_UID);
        when(mMockAsrService.signOutWithObservable(FAKE_ACCESS_TOKEN, FAKE_CLIENT, FAKE_UID))
                .thenReturn(Observable.just(Response.success(signOutResponse)));
        mDataManager.signOut();
        verify(mMockAsrService).signOutWithObservable(FAKE_ACCESS_TOKEN, FAKE_CLIENT, FAKE_UID);
    }

    @Test
    public void testSaveAuthorizationResponse() throws Exception {
        okhttp3.Response okHttpResponse = mOkHttpResponseBuilder
                .addHeader(Constants.HEADER_FIELD_TOKEN, FAKE_ACCESS_TOKEN)
                .addHeader(Constants.HEADER_FIELD_CLIENT, FAKE_CLIENT)
                .addHeader(Constants.HEADER_FIELD_UID, FAKE_UID)
                .build();

        SignInResponse signInResponse = new SignInResponse();
        signInResponse.setUser(new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL));
        Response<SignInResponse> response = Response.success(signInResponse, okHttpResponse);
        mDataManager.saveAuthorizationResponse(response);
        verify(mMockPrefsHelper).setAuthAccessToken(FAKE_ACCESS_TOKEN);
        verify(mMockPrefsHelper).setAuthClient(FAKE_CLIENT);
        verify(mMockPrefsHelper).setAuthUid(FAKE_UID);
        verify(mMockPrefsHelper).setCurrentUser(response.body().getUser());
    }

    @Test
    public void testIsLoggedWithToken() throws Exception {
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);
        assertTrue(mDataManager.isLoggedWithToken());
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn("");
        assertFalse(mDataManager.isLoggedWithToken());
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        User fakeUser = new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL);
        when(mMockPrefsHelper.getCurrentUser())
                .thenReturn(fakeUser);
        Assert.assertEquals(fakeUser, mDataManager.getCurrentUser());
    }
}