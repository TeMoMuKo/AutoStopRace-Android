package pl.temomuko.autostoprace;

import android.app.Activity;
import android.location.Address;
import android.net.Uri;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.domain.model.LocationRecord;
import pl.temomuko.autostoprace.ui.post.PostMvpView;
import pl.temomuko.autostoprace.ui.post.PostPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import rx.Completable;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-01-30.
 */
@RunWith(MockitoJUnitRunner.class)
public class PostPresenterTest {

    private static final String FAKE_MESSAGE = "fake_message";

    @Mock PostMvpView mMockPostMvpView;
    @Mock DataManager mMockDataManager;
    @Mock Address mMockLatestAddress;
    private PostPresenter mPostPresenter;

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
                .thenReturn(Completable.complete());
        when(mMockLatestAddress.getLatitude()).thenReturn(12.34);
        when(mMockLatestAddress.getLongitude()).thenReturn(45.67);
        Uri mockUri = mock(Uri.class);
        when(mockUri.toString()).thenReturn(TestConstants.TEST_IMAGE_URL);

        //when
        mPostPresenter.tryToSaveLocation(FAKE_MESSAGE, mockUri);

        //then
        verify(mMockDataManager).saveUnsentLocationRecordToDatabase(any(LocationRecord.class));
        verify(mMockPostMvpView).showSuccessInfo();
        verify(mMockPostMvpView).closeActivityWithSuccessCode();
    }

    @Test
    public void testStartLocationServiceWithoutPermission() {
        //given
        when(mMockDataManager.hasFineLocationPermission()).thenReturn(false);

        //when
        mPostPresenter.startLocationService();

        //then
        verify(mMockDataManager, never()).checkLocationSettings();
        verify(mMockPostMvpView).clearCurrentLocation();
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
        verify(mMockPostMvpView).clearCurrentLocation();
        verify(mMockDataManager).checkLocationSettings();
        verify(mMockPostMvpView, never()).compatRequestFineLocationPermission();
    }

    @Test
    public void testHandlePermissionResultGranted() throws Exception {
        //given
        when(mMockDataManager.checkLocationSettings())
                .thenReturn(Observable.empty());

        //when
        mPostPresenter.handleLocationPermissionResult(true);

        //then
        verify(mMockDataManager).checkLocationSettings();
        verify(mMockPostMvpView, never()).finishWithInadequateSettingsWarning();
    }

    @Test
    public void testHandlePermissionResultDenied() throws Exception {
        //when
        mPostPresenter.handleLocationPermissionResult(false);

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
        //when
        mPostPresenter.handleLocationSettingsDialogResult(Activity.RESULT_CANCELED);

        //then
        verify(mMockDataManager, never()).getDeviceLocation();
        verify(mMockPostMvpView).finishWithInadequateSettingsWarning();
    }
}