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
import pl.temomuko.autostoprace.TestConstants;
import pl.temomuko.autostoprace.data.local.PermissionHelper;
import pl.temomuko.autostoprace.data.local.Preferences;
import pl.temomuko.autostoprace.data.local.csv.ContactHelper;
import pl.temomuko.autostoprace.data.local.csv.PhrasebookHelper;
import pl.temomuko.autostoprace.data.local.database.DatabaseHelper;
import pl.temomuko.autostoprace.data.local.geocoding.GeocodingHelper;
import pl.temomuko.autostoprace.data.local.gms.GmsLocationHelper;
import pl.temomuko.autostoprace.data.local.photo.ImageController;
import pl.temomuko.autostoprace.domain.model.LocationRecord;
import pl.temomuko.autostoprace.domain.model.User;
import rx.Completable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-01-27.
 */

@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {

    private static final String FAKE_EMAIL = "fake_email";
    private static final String FAKE_PASS = "fake_pass";
    private static final String FAKE_ACCESS_TOKEN = "fake_access_token";
    private static final String FAKE_CLIENT = "fake_client";
    private static final String FAKE_UID = "fake_uid";
    private static final String FAKE_FIRST_NAME = "fake_first_name";
    private static final String FAKE_LAST_NAME = "fake_last_name";

    @Mock
    Preferences mMockPreferences;
    @Mock
    DatabaseHelper mMockDatabaseHelper;
    @Mock
    GmsLocationHelper mMockGmsLocationHelper;
    @Mock
    PermissionHelper mMockPermissionHelper;
    @Mock
    GeocodingHelper mMockGeocodingHelper;
    @Mock
    PhrasebookHelper mPhrasebookHelper;
    @Mock
    ContactHelper mContactHelper;
    @Mock
    ImageController mImageController;
    private DataManager mDataManager;

    private okhttp3.Response.Builder mOkHttpResponseBuilder;

    @Before
    public void setUp() throws Exception {
        mDataManager = new DataManager(mMockPreferences, mMockDatabaseHelper,
                mMockGmsLocationHelper, mMockPermissionHelper, mMockGeocodingHelper, mPhrasebookHelper,
                mContactHelper, mImageController);
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
        LocationRecord unsentLocationRecord = TestConstants.PROPER_LOCATION_RECORD;
        when(mMockDatabaseHelper.addUnsentLocationRecord(unsentLocationRecord)).thenReturn(Completable.complete());

        //when
        mDataManager.saveUnsentLocationRecordToDatabase(unsentLocationRecord);

        //then
        verify(mMockDatabaseHelper).addUnsentLocationRecord(unsentLocationRecord);
    }

    @Test
    public void testSaveSentLocationsToDatabase() throws Exception {
        //given
        LocationRecord sentLocationRecord = TestConstants.PROPER_LOCATION_RECORD;

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
    public void testSaveUser() throws Exception {
        //given
        User user = mock(User.class);

        //when
        mDataManager.saveUser(user);

        //then
        verify(mMockPreferences).setCurrentUser(user);
    }

    @Test
    public void testIsLoggedWithToken() throws Exception {
        //given
        when(mMockPreferences.getAuthAccessToken()).thenReturn(FAKE_ACCESS_TOKEN);

        //assert
        assertTrue(mDataManager.isLoggedWithToken());

        //given
        when(mMockPreferences.getAuthAccessToken()).thenReturn("");

        //assert
        assertFalse(mDataManager.isLoggedWithToken());
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        //given
        User fakeUser = new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL);
        when(mMockPreferences.getCurrentUser()).thenReturn(fakeUser);

        //assert
        Assert.assertEquals(fakeUser, mDataManager.getCurrentUser());
    }
}
