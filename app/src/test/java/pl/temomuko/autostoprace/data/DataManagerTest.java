package pl.temomuko.autostoprace.data;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.local.PermissionHelper;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.local.csv.PhrasebookHelper;
import pl.temomuko.autostoprace.data.local.database.DatabaseHelper;
import pl.temomuko.autostoprace.data.local.geocoding.GeocodingHelper;
import pl.temomuko.autostoprace.data.local.gms.GmsLocationHelper;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.ApiManager;
import pl.temomuko.autostoprace.service.helper.UnsentAndResponseLocationRecordPair;
import retrofit2.Response;
import rx.Observable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-01-27.
 */

@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {

    private static String FAKE_EMAIL = "fake_email";
    private static String FAKE_PASS = "fake_pass";
    private static String FAKE_ACCESS_TOKEN = "fake_access_token";
    private static String FAKE_CLIENT = "fake_client";
    private static String FAKE_UID = "fake_uid";
    private static String FAKE_FIRST_NAME = "fake_first_name";
    private static String FAKE_LAST_NAME = "fake_last_name";

    @Mock PrefsHelper mMockPrefsHelper;
    @Mock ApiManager mMockApiManager;
    @Mock DatabaseHelper mMockDatabaseHelper;
    @Mock GmsLocationHelper mMockGmsLocationHelper;
    @Mock PermissionHelper mMockPermissionHelper;
    @Mock GeocodingHelper mMockGeocodingHelper;
    @Mock PhrasebookHelper mPhrasebookHelper;
    private DataManager mDataManager;

    private okhttp3.Response.Builder mOkHttpResponseBuilder;

    @Before
    public void setUp() throws Exception {
        mDataManager = new DataManager(mMockApiManager, mMockPrefsHelper, mMockDatabaseHelper,
                mMockGmsLocationHelper, mMockPermissionHelper, mMockGeocodingHelper, mPhrasebookHelper);
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
    public void testSaveUnsentLocationsToDatabase() throws Exception {
        //given
        LocationRecord unsentLocationRecord = new LocationRecord(18.05, 17.17, "Yo", "Somewhere, Poland", "Poland", "PL");
        when(mMockDatabaseHelper.addUnsentLocationRecord(unsentLocationRecord)).thenReturn(Observable.empty());

        //when
        mDataManager.saveUnsentLocationRecordToDatabase(unsentLocationRecord);

        //then
        verify(mMockDatabaseHelper).addUnsentLocationRecord(unsentLocationRecord);
    }

    @Test
    public void testSaveSentLocationsToDatabase() throws Exception {
        //given
        LocationRecord sentLocationRecord = new LocationRecord(18.05, 17.17, "Yo", "Somewhere, Poland", "Poland", "PL");
        UnsentAndResponseLocationRecordPair pair = UnsentAndResponseLocationRecordPair.create(sentLocationRecord, null);
        when(mMockDatabaseHelper.moveLocationRecordToSent(pair)).thenReturn(Observable.empty());

        //when
        mDataManager.saveUnsentLocationRecordToDatabase(sentLocationRecord);

        //then
        verify(mMockDatabaseHelper).addUnsentLocationRecord(sentLocationRecord);
    }

    @Test
    public void testGetTeamLocationsFromDatabase() throws Exception {
        //when
        mDataManager.getTeamLocationRecordsFromDatabase();

        //then
        verify(mMockDatabaseHelper).getLocationRecordList();
    }

    @Test
    public void testSaveAuthorizationResponse() throws Exception {
        //given
        okhttp3.Response okHttpResponse = mOkHttpResponseBuilder
                .addHeader(Constants.HEADER_FIELD_TOKEN, FAKE_ACCESS_TOKEN)
                .addHeader(Constants.HEADER_FIELD_CLIENT, FAKE_CLIENT)
                .addHeader(Constants.HEADER_FIELD_UID, FAKE_UID)
                .build();

        SignInResponse signInResponse = new SignInResponse();
        signInResponse.setUser(new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL));
        Response<SignInResponse> response = Response.success(signInResponse, okHttpResponse);

        //when
        mDataManager.saveAuthorizationResponse(response);

        //then
        verify(mMockPrefsHelper).setAuthorizationHeaders(response.headers());
        verify(mMockPrefsHelper).setCurrentUser(response.body().getUser());
    }

    @Test
    public void testIsLoggedWithToken() throws Exception {
        //given
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);

        //assert
        assertTrue(mDataManager.isLoggedWithToken());

        //given
        when(mMockPrefsHelper.getAuthAccessToken()).thenReturn("");

        //assert
        assertFalse(mDataManager.isLoggedWithToken());
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        //given
        User fakeUser = new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL);
        when(mMockPrefsHelper.getCurrentUser()).thenReturn(fakeUser);

        //assert
        Assert.assertEquals(fakeUser, mDataManager.getCurrentUser());
    }
}