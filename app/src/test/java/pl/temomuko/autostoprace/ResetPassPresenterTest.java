package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.ResetPassResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.login.resetpass.ResetPassMvpView;
import pl.temomuko.autostoprace.ui.login.resetpass.ResetPassPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import retrofit2.Response;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-03-20.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResetPassPresenterTest {

    @Mock ResetPassMvpView mMockResetPassMvpView;
    @Mock DataManager mMockDataManager;
    @Mock ErrorHandler mMockErrorHandler;
    @Mock RxCacheHelper<Response<ResetPassResponse>> mMockRxResetHelper;
    private ResetPassPresenter mResetPassPresenter;
    private static final String FAKE_EMAIL = "fake@email.pl";

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
        resetResponse.setUser(new User(1, 1, "Jan", "Kowalski", "jan@kow.pl"));
        Response<ResetPassResponse> response = Response.success(resetResponse);
        when(mMockErrorHandler.isEmailValid(FAKE_EMAIL)).thenReturn(true);
        Observable<Response<ResetPassResponse>> resetResponseObservable = Observable.just(response);
        when(mMockDataManager.resetPassword(FAKE_EMAIL)).thenReturn(resetResponseObservable);
        when(mMockRxResetHelper.getRestoredCachedObservable()).thenReturn(Observable.just(response));
        when(mMockDataManager.requireHttpStatus(response, HttpStatus.OK)).thenReturn(Observable.just(response));

        //when
        mResetPassPresenter.resetPassword(FAKE_EMAIL);

        //then
        verify(mMockResetPassMvpView).showSuccessInfo("jan@kow.pl");
        verify(mMockResetPassMvpView).finish();
        verify(mMockResetPassMvpView, never()).showError(any(String.class));
    }

    //TODO: 20.03.2016 Rest of tests.
}