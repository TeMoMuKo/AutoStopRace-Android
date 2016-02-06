package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.ui.settings.SettingsMvpView;
import pl.temomuko.autostoprace.ui.settings.SettingsPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import retrofit2.Response;
import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by szymen on 2016-02-06.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingsPresenterTest {

    @Mock SettingsMvpView mMockSettingsMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    private SettingsPresenter mSettingsPresenter;
    private static final String FAKE_FIRST_NAME = "fake_first_name";
    private static final String FAKE_LAST_NAME = "fake_last_name";
    private static final String FAKE_EMAIL = "fake_email";

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mSettingsPresenter = new SettingsPresenter(mMockDataManager);
        mSettingsPresenter.attachView(mMockSettingsMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mSettingsPresenter.detachView();
    }

    @Test
    public void testSetupLogoutPreferenceForLoggedUser() throws Exception {
        when(mMockDataManager.isLoggedWithToken()).thenReturn(true);
        User fakeUser = new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL);
        when(mMockDataManager.getCurrentUser()).thenReturn(fakeUser);
        mSettingsPresenter.setupLogoutPreference();
        verify(mMockSettingsMvpView).setupLogoutPreferenceEnabled(true);
        verify(mMockSettingsMvpView)
                .setupLogoutPreferenceSummary(true, FAKE_FIRST_NAME.concat(" ").concat(FAKE_LAST_NAME));
    }

    @Test
    public void testSetupLogoutPreferenceForGuest() throws Exception {
        when(mMockDataManager.isLoggedWithToken()).thenReturn(false);
        User fakeUser = new User(1, 1, "", "", "");
        when(mMockDataManager.getCurrentUser()).thenReturn(fakeUser);
        mSettingsPresenter.setupLogoutPreference();
        verify(mMockSettingsMvpView).setupLogoutPreferenceEnabled(false);
        verify(mMockSettingsMvpView).setupLogoutPreferenceSummary(false, " ");
    }

    @Test
    public void testLogout() throws Exception {
        when(mMockDataManager.signOut())
                .thenReturn(Observable.<Response<SignOutResponse>>empty());
        mSettingsPresenter.logout();
        verify(mMockDataManager).clearUserData();
        verify(mMockSettingsMvpView).showLogoutMessage();
        verify(mMockSettingsMvpView).startLauncherActivity();
    }
}