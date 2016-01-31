package pl.temomuko.autostoprace;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

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
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatus;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import retrofit.Response;
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
    @Mock ErrorHandler mMockErrorHandler;
    private LoginPresenter mLoginPresenter;
    private static final String FAKE_EMAIL = "fake_email";
    private static final String FAKE_PASS = "fake_pass";
    private static final String FAKE_ERROR_MESSAGE = "fake_error_message";

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mLoginPresenter = new LoginPresenter(mMockDataManager, mMockErrorHandler);
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
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS))
                .thenReturn(Observable.just(response));
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);
        verify(mMockDataManager).saveAuthorizationResponse(response);
        verify(mMockLoginMvpView).startMainActivity();
        verify(mMockLoginMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testSignInFails() throws Exception {
        Response<SignInResponse> response = Response.error(HttpStatus.UNAUTHORIZED,
                ResponseBody.create(
                        MediaType.parse("application/json"),
                        "{ \"errors\": [ \"Invalid login credentials. Please try again.\" ] }"
                ));
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS)).thenReturn(Observable.just(response));
        when(mMockErrorHandler.getMessage(response)).thenReturn(FAKE_ERROR_MESSAGE);
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);
        verify(mMockLoginMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockDataManager, never()).saveAuthorizationResponse(response);
        verify(mMockLoginMvpView, never()).startMainActivity();
    }
}