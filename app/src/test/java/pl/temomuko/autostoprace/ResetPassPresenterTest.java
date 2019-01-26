package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.ResetPassResponse;
import pl.temomuko.autostoprace.domain.model.User;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.ui.login.resetpass.ResetPassMvpView;
import pl.temomuko.autostoprace.ui.login.resetpass.ResetPassPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import retrofit2.Response;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-03-20.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResetPassPresenterTest {

    private static final String FAKE_EMAIL = "fake@email.pl";
    private static final String FAKE_ERROR_MESSAGE = "fake_error_message";
    private static final String FAKE_FIRST_NAME = "fake_first_name";
    private static final String FAKE_LAST_NAME = "fake_last_name";
    private static final String NOT_FOUND_USER_RESPONSE = "{\n" +
            "  \"success\": false,\n" +
            "  \"errors\": [\n" +
            "    \"Unable to find user with email 'fake@email.pl'.\"\n" +
            "  ]\n" +
            "}";

    @Mock ResetPassMvpView mMockResetPassMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    @Mock RxCacheHelper<Response<ResetPassResponse>> mMockRxResetHelper;
    private ResetPassPresenter mResetPassPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mResetPassPresenter = new ResetPassPresenter(mMockDataManager, mMockErrorHandler);
        mResetPassPresenter.setupRxCacheHelper(null, mMockRxResetHelper);
        when(mMockRxResetHelper.isCached()).thenReturn(false);
        mResetPassPresenter.attachView(mMockResetPassMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mResetPassPresenter.detachView();
    }

    @Test
    public void testResetPassSuccess() throws Exception {
        //given
        ResetPassResponse resetResponse = new ResetPassResponse();
        resetResponse.setUser(new User(1, 1, FAKE_FIRST_NAME, FAKE_LAST_NAME, FAKE_EMAIL));
        Response<ResetPassResponse> response = Response.success(resetResponse);
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        Observable<Response<ResetPassResponse>> resetResponseObservable = Observable.just(response);
        when(mMockDataManager.resetPassword(FAKE_EMAIL)).thenReturn(resetResponseObservable);
        when(mMockRxResetHelper.getRestoredCachedObservable()).thenReturn(Observable.just(response));

        //when
        mResetPassPresenter.resetPassword(FAKE_EMAIL);

        //then
        verify(mMockResetPassMvpView).showSuccessInfo();
        verify(mMockResetPassMvpView).setProgress(false);
        verify(mMockResetPassMvpView).finish();
        verify(mMockResetPassMvpView, never()).showError(any(String.class));
    }

    @Test
    public void testResetPassFails() throws Exception {
        //given
        Response<ResetPassResponse> response = Response.error(HttpStatus.NOT_FOUND,
                ResponseBody.create(
                        MediaType.parse(Constants.HEADER_VALUE_APPLICATION_JSON), NOT_FOUND_USER_RESPONSE
                ));
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        when(mMockDataManager.resetPassword(FAKE_EMAIL)).thenReturn(Observable.just(response));
        StandardResponseException responseException = new StandardResponseException(response);
        when(mMockErrorHandler.getMessage(responseException)).thenReturn(FAKE_ERROR_MESSAGE);
        when(mMockRxResetHelper.getRestoredCachedObservable()).thenReturn(Observable.error(responseException));

        //when
        mResetPassPresenter.resetPassword(FAKE_EMAIL);

        //then
        verify(mMockResetPassMvpView).showError(FAKE_ERROR_MESSAGE);
        verify(mMockResetPassMvpView).setProgress(false);
        verify(mMockResetPassMvpView, never()).showSuccessInfo();
        verify(mMockResetPassMvpView, never()).finish();
    }

    @Test
    public void testSignInInvalidForm() throws Exception {
        //given
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(false);

        //when
        mResetPassPresenter.resetPassword(FAKE_EMAIL);

        //then
        verify(mMockResetPassMvpView).setInvalidEmailValidationError(true);
        verify(mMockResetPassMvpView, never()).showSuccessInfo();
        verify(mMockResetPassMvpView, never()).showError(any(String.class));
        verify(mMockResetPassMvpView, never()).setProgress(anyBoolean());
        verify(mMockResetPassMvpView, never()).finish();
    }
}