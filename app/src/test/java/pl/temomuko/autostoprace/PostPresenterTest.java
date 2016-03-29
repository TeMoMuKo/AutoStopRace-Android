package pl.temomuko.autostoprace;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.post.PostMvpView;
import pl.temomuko.autostoprace.ui.post.PostPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-01-30.
 */
@RunWith(MockitoJUnitRunner.class)
public class PostPresenterTest {

    @Mock PostMvpView mMockPostMvpView;
    @Mock DataManager mMockDataManager;
    @Mock Address mMockLatestAddress;
    private PostPresenter mPostPresenter;
    private final static String FAKE_MESSAGE = "fake_message";
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mPostPresenter = new PostPresenter(mMockDataManager);
        mPostPresenter.setLatestAddress(mMockLatestAddress);
        mPostPresenter.attachView(mMockPostMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mPostPresenter.detachView();
    }

    @Test
    public void testSaveLocation() throws Exception {
        //given
        when(mMockDataManager.saveUnsentLocationRecordToDatabase(any(LocationRecord.class)))
                .thenReturn(Observable.<LocationRecord>empty());
        when(mMockLatestAddress.getLatitude()).thenReturn(12.34);
        when(mMockLatestAddress.getLongitude()).thenReturn(45.67);

        //when
        mPostPresenter.tryToSaveLocation(FAKE_MESSAGE);

        //then
        verify(mMockDataManager).saveUnsentLocationRecordToDatabase(any(LocationRecord.class));
        verify(mMockPostMvpView).closeActivity();
    }

    @Test
    public void testStartLocationServiceWithoutPermission() {
        //given
        when(mMockDataManager.hasFineLocationPermission()).thenReturn(false);

        //when
        mPostPresenter.startLocationService();

        //then
        //verify(mMockDataManager, never()).checkLocationSettings(any(LocationRequest.class));
        verify(mMockPostMvpView).compatRequestFineLocationPermission();
    }

    @Test
    public void testStartLocationServiceWithPermission() {
        //given
        when(mMockDataManager.hasFineLocationPermission()).thenReturn(true);
        when(mMockDataManager.checkLocationSettings()).thenReturn(Observable.empty());

        //when
        mPostPresenter.startLocationService();

        //then
        verify(mMockDataManager).checkLocationSettings();
        verify(mMockPostMvpView, never()).compatRequestFineLocationPermission();
    }

    @Test
    public void testHandlePermissionResultGranted() throws Exception {
        //given
        when(mMockDataManager.checkLocationSettings())
                .thenReturn(Observable.empty());

        //when
        mPostPresenter.handleLocationPermissionResult(FINE_LOCATION_PERMISSION_REQUEST_CODE,
                new int[]{PackageManager.PERMISSION_GRANTED});

        //then
        verify(mMockDataManager).checkLocationSettings();
        verify(mMockPostMvpView, never()).finishWithInadequateSettingsWarning();
    }

    @Test
    public void testHandlePermissionResultDenied() throws Exception {
        //given
        when(mMockDataManager.checkLocationSettings())
                .thenReturn(Observable.empty());

        //when
        mPostPresenter.handleLocationPermissionResult(FINE_LOCATION_PERMISSION_REQUEST_CODE,
                new int[]{PackageManager.PERMISSION_DENIED});

        //then
        verify(mMockDataManager, never()).checkLocationSettings();
        verify(mMockPostMvpView).finishWithInadequateSettingsWarning();
    }

    @Test
    public void testHandleLocationSettingsDialogResultOk() {
        //given
        when(mMockDataManager.getDeviceLocation())
                .thenReturn(Observable.empty());

        //when
        mPostPresenter.handleLocationSettingsDialogResult(Activity.RESULT_OK);

        //then
        verify(mMockDataManager).getDeviceLocation();
        verify(mMockPostMvpView, never()).finishWithInadequateSettingsWarning();
    }

    @Test
    public void testHandleLocationSettingsDialogResultCanceled() {
        //given
        when(mMockDataManager.getDeviceLocation())
                .thenReturn(Observable.empty());

        //when
        mPostPresenter.handleLocationSettingsDialogResult(Activity.RESULT_CANCELED);

        //then
        verify(mMockDataManager, never()).getDeviceLocation();
        verify(mMockPostMvpView).finishWithInadequateSettingsWarning();
    }
}