package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.login.LoginMvpView;
import pl.temomuko.autostoprace.ui.login.LoginPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by szymen on 2016-01-27.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock LoginMvpView mMockLoginMvpView;
    @Mock DataManager mMockDataManager;
    private LoginPresenter mLoginPresenter;
    private static String FAKE_EMAIL = "fake_email";
    private static String FAKE_PASS = "fake_pass";

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mLoginPresenter = new LoginPresenter(mMockDataManager);
        mLoginPresenter.attachView(mMockLoginMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mLoginPresenter.detachView();
    }

    @Test
    public void testSignInSuccess() throws Exception {
        SignInResponse signInResponse = new SignInResponse();
        retrofit.Response<SignInResponse> response = retrofit.Response.success(signInResponse);
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS))
                .thenReturn(Observable.just(response));
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);
        verify(mMockDataManager).saveAuthorizationResponse(response);
        verify(mMockLoginMvpView).startMainActivity();
        verify(mMockLoginMvpView, never()).showError(any(String.class));
    }
}