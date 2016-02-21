package pl.temomuko.autostoprace.data;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.local.PermissionHelper;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.local.database.DatabaseHelper;
import pl.temomuko.autostoprace.data.local.geocoding.GeocodingHelper;
import pl.temomuko.autostoprace.data.local.gms.GmsLocationHelper;
import pl.temomuko.autostoprace.data.model.CreateLocationRecordRequest;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.AsrService;
import retrofit2.Response;
import rx.Observable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-01-27.
 */

@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {

    @Mock PrefsHelper mMockPrefsHelper;
    @Mock AsrService mMockAsrService;
    @Mock DatabaseHelper mMockDatabaseHelper;
    @Mock GmsLocationHelper mMockGmsLocationHelper;
    @Mock PermissionHelper mMockPermissionHelper;
    @Mock GeocodingHelper mMockGeocodingHelper;
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
        mDataManager = new DataManager(mMockAsrService, mMockPrefsHelper, mMockDatabaseHelper,
                mMockGmsLocationHelper, mMockPermissionHelper, mMockGeocodingHelper);
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
        when(mMockPrefsHelper.getCurrentUser()).thenReturn(new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL));
        Observable<Response<List<LocationRecord>>> expectedObservable =
                mMockAsrService.getLocationRecordsWithObservable(mMockPrefsHelper.getCurrentUser().getTeamId());
        Observable<Response<List<LocationRecord>>> actualObservable =
                mDataManager.getTeamLocationRecordsFromServer();
        assertEquals(expectedObservable, actualObservable);
    }

    @Test
    public void testValidateToken() throws Exception {
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);
        when(mMockPrefsHelper.getAuthClient()).thenReturn(FAKE_CLIENT);
        when(mMockPrefsHelper.getAuthUid()).thenReturn(FAKE_UID);
        mDataManager.validateToken();
        verify(mMockAsrService).validateTokenWithObservable(FAKE_ACCESS_TOKEN, FAKE_CLIENT, FAKE_UID);
    }

    @Test
    public void testSaveUnsentLocationsToDatabase() throws Exception {
        LocationRecord unsentLocationRecord = new LocationRecord(18.05, 17.17, "");
        when(mMockDatabaseHelper.addUnsentLocationRecord(unsentLocationRecord)).thenReturn(Observable.empty());
        mDataManager.saveUnsentLocationRecordToDatabase(unsentLocationRecord);
        verify(mMockDatabaseHelper).addUnsentLocationRecord(unsentLocationRecord);
    }

    @Test
    public void testSaveSentLocationsToDatabase() throws Exception {
        LocationRecord sentLocationRecord = new LocationRecord(18.05, 17.17, "");
        when(mMockDatabaseHelper.addSentLocationRecord(sentLocationRecord)).thenReturn(Observable.empty());
        mDataManager.saveUnsentLocationRecordToDatabase(sentLocationRecord);
        verify(mMockDatabaseHelper).addUnsentLocationRecord(sentLocationRecord);
    }

    @Test
    public void testGetTeamLocationsFromDatabase() throws Exception {
        mDataManager.getTeamLocationRecordsFromDatabase();
        verify(mMockDatabaseHelper).getUnsentLocationRecordList();
        verify(mMockDatabaseHelper).getSentLocationRecordList();
    }

    @Test
    public void testPostLocationToServer() throws Exception {
        LocationRecord locationRecordToSend = new LocationRecord(12.34, 56.78, "");
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);
        when(mMockPrefsHelper.getAuthClient()).thenReturn(FAKE_CLIENT);
        when(mMockPrefsHelper.getAuthUid()).thenReturn(FAKE_UID);
        mDataManager.postLocationRecordToServer(locationRecordToSend);
        verify(mMockAsrService).postLocationRecordWithObservable(
                eq(FAKE_ACCESS_TOKEN), eq(FAKE_CLIENT), eq(FAKE_UID), any(CreateLocationRecordRequest.class));
    }

    @Test
    public void testSignIn() throws Exception {
        Observable<Response<SignInResponse>> expectedObservable
                = mMockAsrService.signInWithObservable(FAKE_EMAIL, FAKE_PASS);
        Observable<Response<SignInResponse>> actualObservable = mDataManager.signIn(FAKE_EMAIL, FAKE_PASS);
        assertEquals(expectedObservable, actualObservable);
    }

    @Test
    public void testSignOut() throws Exception {
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);
        when(mMockPrefsHelper.getAuthClient()).thenReturn(FAKE_CLIENT);
        when(mMockPrefsHelper.getAuthUid()).thenReturn(FAKE_UID);
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
        verify(mMockPrefsHelper).setAuthorizationHeaders(response.headers());
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