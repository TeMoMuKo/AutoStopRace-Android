package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.post.PostMvpView;
import pl.temomuko.autostoprace.ui.post.PostPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;

/**
 * Created by szymen on 2016-01-30.
 */
@RunWith(MockitoJUnitRunner.class)
public class PostPresenterTest {

    @Mock PostMvpView mMockPostMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    private PostPresenter mPostPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mPostPresenter = new PostPresenter(mMockDataManager, mMockErrorHandler);
        mPostPresenter.attachView(mMockPostMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mPostPresenter.detachView();
    }

    @Test
    public void testSaveLocation() throws Exception {
        //TODO
    }
}