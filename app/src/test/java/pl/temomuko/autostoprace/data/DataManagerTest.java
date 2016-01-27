package pl.temomuko.autostoprace.data;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;

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
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.ApiManager;
import retrofit.Response;
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
    @Mock ApiManager mMockApiManager;
    private DataManager mDataManager;
    private static String FAKE_LOGIN = "fake_login";
    private static String FAKE_PASS = "fake_pass";
    private static String FAKE_ACCESS_TOKEN = "fake_access_token";
    private static String FAKE_CLIENT = "fake_client";
    private static String FAKE_UID = "fake_uid";
    private com.squareup.okhttp.Response.Builder mOkHttpResponseBuilder;

    @Before
    public void setUp() throws Exception {
        mDataManager = new DataManager(mMockApiManager, mMockPrefsHelper);
        setupFakeResponseBuilder();
    }

    private void setupFakeResponseBuilder() {
        HttpUrl httpUrl = new HttpUrl.Builder().scheme("https").host("api.autostoprace.pl").build();
        Request request = new Request.Builder().url(httpUrl).build();
        mOkHttpResponseBuilder = new com.squareup.okhttp.Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200);
    }

    @Test
    public void testGetTeamLocationsFromServer() throws Exception {
        List<Location> locations = new ArrayList<>();
        when(mMockPrefsHelper.getCurrentUser()).thenReturn(new User(1, 1, "Janek", "Kowalski", "jan@kow.pl"));
        when(mMockApiManager.getLocationsWithObservable(1)).thenReturn(Observable.just(locations));
        Observable<List<Location>> expectedObservable =
                mMockApiManager.getLocationsWithObservable(mMockPrefsHelper.getCurrentUser().getTeamId());
        Observable<List<Location>> actualObservable = mDataManager.getTeamLocationsFromServer();
        assertEquals(expectedObservable, actualObservable);
    }

    @Test
    public void testSaveLocationsToDatabase() throws Exception {
        //TODO
    }

    @Test
    public void testSignIn() throws Exception {
        when(mMockApiManager.signInWithObservable(FAKE_LOGIN, FAKE_PASS))
                .thenReturn(Observable.<Response<SignInResponse>>empty());
        Observable<Response<SignInResponse>> expectedObservable
                = mMockApiManager.signInWithObservable(FAKE_LOGIN, FAKE_PASS);
        Observable<Response<SignInResponse>> actualObservable = mDataManager.signIn(FAKE_LOGIN, FAKE_PASS);
        assertEquals(expectedObservable, actualObservable);
    }

    @Test
    public void testSignOut() throws Exception {
        SignOutResponse signOutResponse = new SignOutResponse();
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);
        when(mMockPrefsHelper.getAuthClient()).thenReturn(FAKE_CLIENT);
        when(mMockPrefsHelper.getAuthUid()).thenReturn(FAKE_UID);
        when(mMockApiManager.signOutWithObservable(FAKE_ACCESS_TOKEN, FAKE_CLIENT, FAKE_UID))
                .thenReturn(Observable.just(Response.success(signOutResponse)));
        mDataManager.signOut();
        verify(mMockApiManager).signOutWithObservable(FAKE_ACCESS_TOKEN, FAKE_CLIENT, FAKE_UID);
    }

    @Test
    public void testSaveAuthorizationResponse() throws Exception {
        com.squareup.okhttp.Response okHttpResponse = mOkHttpResponseBuilder
                .addHeader(Constants.HEADER_ACCESS_TOKEN, FAKE_ACCESS_TOKEN)
                .addHeader(Constants.HEADER_CLIENT, FAKE_CLIENT)
                .addHeader(Constants.HEADER_UID, FAKE_UID)
                .build();

        SignInResponse signInResponse = new SignInResponse();
        signInResponse.setUser(new User(1, 1, "Janek", "Kowalski", "jan@kow.pl"));
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
        User fakeUser = new User(1, 1, "Janek", "Kowalski", "jan@kow.pl");
        when(mMockPrefsHelper.getCurrentUser())
                .thenReturn(fakeUser);
        Assert.assertEquals(fakeUser, mDataManager.getCurrentUser());
    }
}