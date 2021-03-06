package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.domain.model.User;
import pl.temomuko.autostoprace.domain.repository.Authenticator;
import pl.temomuko.autostoprace.ui.settings.SettingsMvpView;
import pl.temomuko.autostoprace.ui.settings.SettingsPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import rx.Completable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-02-06.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingsPresenterTest {

    private static final String FAKE_FIRST_NAME = "fake_first_name";
    private static final String FAKE_LAST_NAME = "fake_last_name";
    private static final String FAKE_EMAIL = "fake_email";

    @Mock SettingsMvpView mMockSettingsMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    @Mock Authenticator authenticator;
    private SettingsPresenter mSettingsPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mSettingsPresenter = new SettingsPresenter(mMockDataManager, authenticator);
        mSettingsPresenter.attachView(mMockSettingsMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mSettingsPresenter.detachView();
    }

    @Test
    public void testSetupLogoutPreferenceForLoggedUser() throws Exception {
        //given
        when(mMockDataManager.isLoggedWithToken()).thenReturn(true);
        User fakeUser = new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL);
        when(mMockDataManager.getCurrentUser()).thenReturn(fakeUser);

        //when
        mSettingsPresenter.setupLogoutPreference();

        //then
        verify(mMockSettingsMvpView).setupLogoutPreferenceEnabled(true);
        verify(mMockSettingsMvpView).setupUserLogoutPreferenceSummary(FAKE_FIRST_NAME.concat(" ").concat(FAKE_LAST_NAME));
    }

    @Test
    public void testSetupLogoutPreferenceForGuest() throws Exception {
        //given
        when(mMockDataManager.isLoggedWithToken()).thenReturn(false);

        //when
        mSettingsPresenter.setupLogoutPreference();

        //then
        verify(mMockSettingsMvpView).setupLogoutPreferenceEnabled(false);
        verify(mMockSettingsMvpView).setupGuestLogoutPreferenceSummary();
    }

    @Test
    public void testLogout() throws Exception {
        //given
        when(authenticator.logout())
                .thenReturn(Completable.complete());
        when(mMockDataManager.clearUserData()).thenReturn(Completable.complete());

        //when
        mSettingsPresenter.logout();

        //then
        verify(mMockDataManager).clearUserData();
        verify(mMockSettingsMvpView).showLogoutMessage();
        verify(mMockSettingsMvpView).disablePostLocationShortcut();
        verify(mMockSettingsMvpView).startLauncherActivity();
    }
}