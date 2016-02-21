package pl.temomuko.autostoprace;

import android.location.Location;

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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by szymen on 2016-01-30.
 */
@RunWith(MockitoJUnitRunner.class)
public class PostPresenterTest {

    @Mock PostMvpView mMockPostMvpView;
    @Mock DataManager mMockDataManager;
    @Mock Location mMockLastestLocation;
    private PostPresenter mPostPresenter;
    private final static String FAKE_MESSAGE = "fake_message";

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mPostPresenter = new PostPresenter(mMockDataManager);
        mPostPresenter.setLatestLocation(mMockLastestLocation);
        mPostPresenter.attachView(mMockPostMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mPostPresenter.detachView();
    }

    @Test
    public void testSaveLocation() throws Exception {
        when(mMockDataManager.saveUnsentLocationRecordToDatabase(any(LocationRecord.class)))
                .thenReturn(rx.Observable.<Void>empty());
        when(mMockLastestLocation.getLatitude()).thenReturn(12.34);
        when(mMockLastestLocation.getLongitude()).thenReturn(45.67);
        mPostPresenter.tryToSaveLocation(FAKE_MESSAGE);
        verify(mMockDataManager).saveUnsentLocationRecordToDatabase(any(LocationRecord.class));
        verify(mMockPostMvpView).startMainActivity();
    }
}