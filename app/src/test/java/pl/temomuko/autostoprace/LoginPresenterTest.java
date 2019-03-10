package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.domain.model.User;
import pl.temomuko.autostoprace.domain.repository.Authenticator;
import pl.temomuko.autostoprace.ui.login.LoginMvpView;
import pl.temomuko.autostoprace.ui.login.LoginPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import retrofit2.HttpException;
import retrofit2.Response;
import rx.Observable;
import rx.Single;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-01-27.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    private static final String FAKE_EMAIL = "fake@email.pl";
    private static final String FAKE_PASS = "fake_pass";
    private static final String INVALID_FAKE_PASS = "";
    private static final String INVALID_FAKE_EMAIL = "fake_email";
    private static final String FAKE_ERROR_MESSAGE = "fake_error_message";
    private static final String UNAUTHORIZED_RESPONSE =
            "{ \"errors\": [ \"Invalid login credentials. Please try again.\" ] }";

    @Mock LoginMvpView mMockLoginMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    @Mock RxCacheHelper<User> mMockRxCacheHelper;
    @Mock Authenticator authenticator;
    private LoginPresenter mLoginPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mLoginPresenter = new LoginPresenter(mMockDataManager, mMockErrorHandler, authenticator);
        mLoginPresenter.setupRxCacheHelper(null, mMockRxCacheHelper);
        when(mMockRxCacheHelper.isCached()).thenReturn(false);
        mLoginPresenter.attachView(mMockLoginMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mLoginPresenter.detachView();
    }

    @Test
    public void testClearCurrentRequestObservableAfterReAttachAndSuccess() throws Exception {
        //given
        User user = mock(User.class);
        when(mMockRxCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(user));
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
        User user = mock(User.class);
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(authenticator.authorize(FAKE_EMAIL, FAKE_PASS)).thenReturn(Single.just(user));
        when(mMockRxCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.just(user));

        //when
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);

        //then
        verify(mMockLoginMvpView).setProgress(true);
        verify(mMockDataManager).saveUser(user);
        verify(mMockLoginMvpView).startMainActivity();
        verify(mMockLoginMvpView).setProgress(false);
        verify(mMockLoginMvpView, never()).showError(any(String.class));
    }

    //todo fix it
    @Ignore
    @Test
    public void testSignInFails() throws Exception {
        //given
        Response response = Response.error(HttpStatus.UNAUTHORIZED,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), UNAUTHORIZED_RESPONSE
                ));
        HttpException exception = new HttpException(response);
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(authenticator.authorize(FAKE_EMAIL, FAKE_PASS)).thenReturn(Single.error(exception));
        StandardResponseException responseException = new StandardResponseException(response);
        when(mMockErrorHandler.getMessage(responseException)).thenReturn(FAKE_ERROR_MESSAGE);
        when(mMockRxCacheHelper.getRestoredCachedObservable()).thenReturn(Observable.error(responseException));

        //when
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);

        //then
        verify(mMockLoginMvpView).setProgress(true);
        verify(mMockLoginMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockLoginMvpView).setProgress(false);
        verify(mMockDataManager, never()).saveUser(any());
        verify(mMockLoginMvpView, never()).startMainActivity();
    }

    @Test
    public void testSignInFailsWithSocketTimeoutException() throws Exception {
        //given
        Throwable fakeException = new SocketTimeoutException();
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(authenticator.authorize(FAKE_EMAIL, FAKE_PASS))
                .thenReturn(Single.error(fakeException));
        when(mMockRxCacheHelper.getRestoredCachedObservable())
                .thenReturn(Observable.error(fakeException));
        when(mMockErrorHandler.getMessage(fakeException))
                .thenReturn(FAKE_ERROR_MESSAGE);

        //when
        mLoginPresenter.signIn(FAKE_EMAIL, FAKE_PASS);

        //then
        verify(mMockLoginMvpView).setProgress(true);
        verify(mMockLoginMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockLoginMvpView).setProgress(false);
        verify(mMockDataManager, never()).saveUser(any());
        verify(mMockLoginMvpView, never()).startMainActivity();
    }

    @Test
    public void testSignInInvalidForm() throws Exception {
        //when
        mLoginPresenter.signIn(INVALID_FAKE_EMAIL, INVALID_FAKE_PASS);

        //then
        verify(mMockLoginMvpView).setInvalidEmailValidationError(true);
        verify(mMockDataManager, never()).saveUser(any());
        verify(mMockLoginMvpView, never()).startMainActivity();
    }
}