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
import retrofit.Response;
import rx.Observable;

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
    private static String FAKE_LOGIN = "fake";
    private static String FAKE_PASS = "fake";

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
        Response<SignInResponse> response = Response.success(signInResponse);
        when(mMockDataManager.signIn(FAKE_LOGIN, FAKE_PASS))
                .thenReturn(Observable.just(response));
        mLoginPresenter.signIn(FAKE_LOGIN, FAKE_PASS);
        verify(mMockDataManager).saveAuthorizationResponse(response);
        verify(mMockLoginMvpView).goToMainActivity();
    }
}