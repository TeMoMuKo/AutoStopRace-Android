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
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.ui.login.LoginMvpView;
import pl.temomuko.autostoprace.ui.login.LoginPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import retrofit2.Response;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-01-27.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock LoginMvpView mMockLoginMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    @Mock RxCacheHelper<Response<SignInResponse>> mMockRxCacheHelper;
    private LoginPresenter mLoginPresenter;
    private static final String FAKE_EMAIL = "fake@email.pl";
    private static final String FAKE_PASS = "fake_pass";
    private static final String INVALID_FAKE_PASS = "";
    private static final String INVALID_FAKE_EMAIL = "fake_email";
    private static final String FAKE_ERROR_MESSAGE = "fake_error_message";
    private static final String UNAUTHORIZED_RESPONSE =
            "{ \"errors\": [ \"Invalid login credentials. Please try again.\" ] }";

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mLoginPresenter = new LoginPresenter(mMockDataManager, mMockErrorHandler);
        mLoginPresenter.setupRxCacheHelper(null, mMockRxCacheHelper);
        when(mMockRxCacheHelper.isCached()).thenReturn(false);
        doNothing().when(mMockRxCacheHelper).setup(null);
        mLoginPresenter.attachView(mMockLoginMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mLoginPresenter.detachView();
    }

    @Test
    public void testClearCurrentRequestObservableAfterReAttachAndSuccess() throws Exception {
        //given
        SignInResponse signInResponse = new SignInResponse();
        Response<SignInResponse> response = Response.success(signInResponse);
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS)).thenReturn(Observable.just(response));
        when(mMockRxCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(response));
        when(mMockDataManager.requireHttpStatus(response)).thenReturn(Observable.just(response));
        when(mMockRxCacheHelper.isCached()).thenReturn(true);

        //when
        mLoginPresenter.attachView(mMockLoginMvpView);

        //then
        verify(mMockRxCacheHelper).clearCache();
    }

    @Test
    public void testCancelSignInRequest() throws Exception {
        mLoginPresenter.cancelSignInRequest();
        verify(mMockRxCacheHelper).clearCache();
    }

    @Test
    public void testSignInSuccess() throws Exception {
        //given
        SignInResponse signInResponse = new SignInResponse();
        Response<SignInResponse> response = Response.success(signInResponse);
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        Observable<Response<SignInResponse>> signInResponseObservable = Observable.just(response);
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS)).thenReturn(signInResponseObservable);
        when(mMockRxCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(response));
        when(mMockDataManager.requireHttpStatus(response)).thenReturn(Observable.just(response));

        //when
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);

        //then
        verify(mMockDataManager).saveAuthorizationResponse(response);
        verify(mMockLoginMvpView).startMainActivity();
        verify(mMockLoginMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testSignInFails() throws Exception {
        //given
        Response<SignInResponse> response = Response.error(HttpStatus.UNAUTHORIZED,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), UNAUTHORIZED_RESPONSE
                ));
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS)).thenReturn(Observable.just(response));
        StandardResponseException responseException = new StandardResponseException(response);
        when(mMockDataManager.requireHttpStatus(response))
                .thenReturn(Observable.error(responseException));
        when(mMockErrorHandler.getMessage(responseException)).thenReturn(FAKE_ERROR_MESSAGE);
        when(mMockRxCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(response));

        //when
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);

        //then
        verify(mMockLoginMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockDataManager, never()).saveAuthorizationResponse(response);
        verify(mMockLoginMvpView, never()).startMainActivity();
    }

    @Test
    public void testSignInFailsWithSocketTimeoutException() throws Exception {
        //given
        Throwable fakeException = new SocketTimeoutException();
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(mMockDataManager.signIn(FAKE_EMAIL, FAKE_PASS))
                .thenReturn(Observable.error(fakeException));
        when(mMockRxCacheHelper.getRestoredCachedObservable())
                .thenReturn(Observable.error(fakeException));
        when(mMockErrorHandler.getMessage(fakeException))
                .thenReturn(FAKE_ERROR_MESSAGE);

        //when
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);

        //then
        verify(mMockLoginMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockDataManager, never()).saveAuthorizationResponse(Matchers.<Response<SignInResponse>>any());
        verify(mMockLoginMvpView, never()).startMainActivity();
    }

    @Test
    public void testSignInInvalidForm() throws Exception {
        //given
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(false);

        //when
        mLoginPresenter.signIn(INVALID_FAKE_EMAIL, INVALID_FAKE_PASS);

        //then
        verify(mMockLoginMvpView).setInvalidEmailValidationError(true);
        verify(mMockLoginMvpView).setInvalidEmailValidationError(true);
        verify(mMockDataManager, never()).saveAuthorizationResponse(Matchers.<Response<SignInResponse>>any());
        verify(mMockLoginMvpView, never()).startMainActivity();
    }
}