package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.ui.main.MainMvpView;
import pl.temomuko.autostoprace.ui.main.MainPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import retrofit.Response;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by szymen on 2016-01-22.
 */

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock MainMvpView mMockMainMvpView;
    @Mock DataManager mMockDataManager;
    private MainPresenter mMainPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mMainPresenter = new MainPresenter(mMockDataManager);
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mMainPresenter.detachView();
    }

    @Test
    public void testLoadLocationsFromApiReturnsLocations() throws Exception {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(12.34, 43.21, ""));
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.just(locations));

        List<Location> locationsFromDatabase = new ArrayList<>();
        locationsFromDatabase.add(new Location(99.99, 99.99, ""));
        when(mMockDataManager.saveLocationsToDatabase(locations))
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocationsFromServer();
        verify(mMockDataManager).saveLocationsToDatabase(locations);
        verify(mMockMainMvpView).updateLocationsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showEmptyInfo();
        verify(mMockMainMvpView, never()).showApiError(any(String.class));
    }

    @Test
    public void testLoadLocationsFromApiReturnsEmptyList() throws Exception {
        List<Location> locations = new ArrayList<>();
        when(mMockDataManager.getTeamLocationsFromServer())
                .thenReturn(Observable.just(locations));

        List<Location> locationsFromDatabase = new ArrayList<>();
        when(mMockDataManager.saveLocationsToDatabase(locations))
                .thenReturn(Observable.just(locationsFromDatabase));

        mMainPresenter.loadLocationsFromServer();
        verify(mMockDataManager).saveLocationsToDatabase(locations);
        verify(mMockMainMvpView).showEmptyInfo();
        verify(mMockMainMvpView, never()).updateLocationsList(locationsFromDatabase);
        verify(mMockMainMvpView, never()).showApiError(any(String.class));
    }

    @Test
    public void testSetupUserInfo() throws Exception {
        User fakeUser = new User(1, 1, "Jan", "Kowalski", "jan@kow.pl");
        when(mMockDataManager.getCurrentUser()).thenReturn(fakeUser);
        mMainPresenter.setupUserInfo();
        verify(mMockMainMvpView).showUser(fakeUser);
    }

    @Test
    public void logout() throws Exception {
        when(mMockDataManager.signOut()).thenReturn(Observable.<Response<SignOutResponse>>empty());
        mMainPresenter.logout();
        verify(mMockDataManager).clearAuth();
        verify(mMockMainMvpView).showLogoutMessage();
        verify(mMockMainMvpView).goToLauncherActivity();
    }
}