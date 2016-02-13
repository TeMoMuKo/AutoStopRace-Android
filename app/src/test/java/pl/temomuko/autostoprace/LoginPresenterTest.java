package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.ui.login.LoginMvpView;
import pl.temomuko.autostoprace.ui.login.LoginPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatusConstants;
import pl.temomuko.autostoprace.util.LoginValidator;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import retrofit2.Response;
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
    @Mock LoginValidator mMockLoginValidator;
    private LoginPresenter mLoginPresenter;
    private static final String FAKE_EMAIL = "fake_email";
    private static final String FAKE_PASS = "fake_pass";
    private static final String FAKE_ERROR_MESSAGE = "fake_error_message";
    private static final String FAKE_EMAIL_VALIDATION_MESSAGE = "fake_email_validation_message";
    private static final String FAKE_PASS_VALIDATION_MESSAGE = "fake_pass_validation_message";
    private static final String UNAUTHORIZED_RESPONSE =
            "{ \"errors\": [ \"Invalid login credentials. Please try again.\" ] }";

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mLoginPresenter = new LoginPresenter(mMockDataManager, mMockErrorHandler, mMockLoginValidator);
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
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS)).thenReturn(Observable.just(response));
        when(mMockDataManager.processLoginResponse(response)).thenReturn(Observable.just(response));
        when(mMockLoginValidator.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(mMockLoginValidator.isPasswordValid(FAKE_PASS)).thenReturn(true);
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);
        verify(mMockDataManager).saveAuthorizationResponse(response);
        verify(mMockLoginMvpView).startMainActivity();
        verify(mMockLoginMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testSignInFails() throws Exception {
        Response<SignInResponse> response = Response.error(HttpStatusConstants.UNAUTHORIZED,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_ACCEPT_JSON), UNAUTHORIZED_RESPONSE
                ));
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS)).thenReturn(Observable.just(response));
        StandardResponseException responseException = new StandardResponseException(response);
        when(mMockDataManager.processLoginResponse(response))
                .thenReturn(Observable.error(responseException));
        when(mMockLoginValidator.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(mMockLoginValidator.isPasswordValid(FAKE_PASS)).thenReturn(true);
        when(mMockErrorHandler.getMessage(responseException)).thenReturn(FAKE_ERROR_MESSAGE);
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);
        verify(mMockLoginMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockDataManager, never()).saveAuthorizationResponse(response);
        verify(mMockLoginMvpView, never()).startMainActivity();
    }

    @Test
    public void testSignInFailsWithSocketTimeoutException() throws Exception {
        Throwable fakeException = new SocketTimeoutException();
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS))
                .thenReturn(Observable.error(fakeException));
        when(mMockLoginValidator.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(mMockLoginValidator.isPasswordValid(FAKE_PASS)).thenReturn(true);
        when(mMockErrorHandler.getMessage(fakeException))
                .thenReturn(FAKE_ERROR_MESSAGE);
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);
        verify(mMockLoginMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockDataManager, never())
                .saveAuthorizationResponse(Matchers.<Response<SignInResponse>>any());
        verify(mMockLoginMvpView, never()).startMainActivity();
    }

    @Test
    public void testSignInInvalidForm() throws Exception {
        when(mMockLoginValidator.isPasswordValid(FAKE_PASS)).thenReturn(false);
        when(mMockLoginValidator.isEmailValid(FAKE_EMAIL)).thenReturn(false);
        when(mMockLoginValidator.getEmailValidErrorMessage(FAKE_EMAIL))
                .thenReturn(FAKE_EMAIL_VALIDATION_MESSAGE);
        when(mMockLoginValidator.getPasswordValidErrorMessage(FAKE_PASS))
                .thenReturn(FAKE_PASS_VALIDATION_MESSAGE);
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);
        verify(mMockLoginMvpView)
                .showEmailValidationError(FAKE_EMAIL_VALIDATION_MESSAGE);
        verify(mMockLoginMvpView)
                .showPasswordValidationError(FAKE_PASS_VALIDATION_MESSAGE);
        verify(mMockDataManager, never())
                .saveAuthorizationResponse(Matchers.<Response<SignInResponse>>any());
        verify(mMockLoginMvpView, never()).startMainActivity();
    }
}