package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.launcher.LauncherMvpView;
import pl.temomuko.autostoprace.ui.launcher.LauncherPresenter;

import static org.mockito.Mockito.verify;

/**
 * Created by Szymon Kozak on 2016-01-30.
 */

@RunWith(MockitoJUnitRunner.class)
public class LauncherPresenterTest {

    @Mock LauncherMvpView mMockLauncherMvpView;
    @Mock DataManager mMockDataManager;
    private LauncherPresenter mLauncherPresenter;

    @Before
    public void setUp() throws Exception {
        mLauncherPresenter = new LauncherPresenter();
        mLauncherPresenter.attachView(mMockLauncherMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mLauncherPresenter.detachView();
    }

    @Test
    public void testGoToLogin() throws Exception {
        mLauncherPresenter.goToLogin();
        verify(mMockLauncherMvpView).startLoginActivity();
    }
}