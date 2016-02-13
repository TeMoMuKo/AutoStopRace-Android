package pl.temomuko.autostoprace.data;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.local.database.DatabaseManager;
import pl.temomuko.autostoprace.data.model.CreateLocationRequest;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.AsrService;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.util.HttpStatus;
import retrofit2.Response;
import rx.Observable;

/**
 * Created by szymen on 2016-01-09.
 */

@Singleton
public class DataManager {

    private AsrService mAsrService;
    private PrefsHelper mPrefsHelper;
    private DatabaseManager mDatabaseManager;

    @Inject
    public DataManager(AsrService asrService, PrefsHelper prefsHelper, DatabaseManager databaseManager) {
        mAsrService = asrService;
        mPrefsHelper = prefsHelper;
        mDatabaseManager = databaseManager;
    }

    public Observable<Response<SignInResponse>> signIn(String login, String password) {
        return mAsrService.signInWithObservable(login, password);
    }

    public Observable<Response<SignOutResponse>> signOut() {
        String accessToken = mPrefsHelper.getAuthAccessToken();
        String client = mPrefsHelper.getAuthClient();
        String uid = mPrefsHelper.getAuthUid();
        return mAsrService.signOutWithObservable(accessToken, client, uid);
    }

    public void clearUserData() {
        mPrefsHelper.clearAuth();
        mDatabaseManager.clearTables().subscribe();
    }

    public Observable<Response<SignInResponse>> validateToken() {
        String accessToken = mPrefsHelper.getAuthAccessToken();
        String client = mPrefsHelper.getAuthClient();
        String uid = mPrefsHelper.getAuthUid();
        return mAsrService.validateTokenWithObservable(accessToken, client, uid);
    }

    public Observable<List<Location>> getTeamLocationsFromDatabase() {
        return Observable.zip(
                mDatabaseManager.getUnsentLocationList(),
                mDatabaseManager.getSentLocationList(),
                (a1, a2) -> {
                    ArrayList<Location> result = new ArrayList<>(a1);
                    result.addAll(a2);
                    return result;
                }
        );
    }

    public Observable<Response<List<Location>>> getTeamLocationsFromServer() {
        return mAsrService.getLocationsWithObservable(mPrefsHelper.getCurrentUser().getTeamId());
    }

    public Observable<List<Location>> syncWithDatabase(Response<List<Location>> response) {
        if (response.code() == HttpStatus.OK) {
            return saveAndEmitLocationsFromDatabase(response.body());
        } else {
            return Observable.error(new StandardResponseException(response));
        }
    }

    private Observable<List<Location>> saveAndEmitLocationsFromDatabase(List<Location> receivedLocations) {
        return Observable.zip(
                mDatabaseManager.getUnsentLocationList(),
                mDatabaseManager.setAndEmitReceivedLocations(receivedLocations),
                (a1, a2) -> {
                    ArrayList<Location> result = new ArrayList<>(a1);
                    result.addAll(a2);
                    return result;
                }
        );
    }

    public Observable<Response<SignInResponse>> processLoginResponse(Response<SignInResponse> response) {
        if (response.code() == HttpStatus.OK) {
            return Observable.just(response);
        } else {
            return Observable.error(new StandardResponseException(response));
        }
    }

    public Observable<Void> saveUnsentLocationToDatabase(Location location) {
        return mDatabaseManager.addUnsentLocation(location);
    }

    public Observable<Location> getUnsentLocations() {
        return mDatabaseManager.getUnsentLocations();
    }

    public Observable<Void> deleteUnsentLocation(Location location) {
        return mDatabaseManager.deleteUnsentLocation(location);
    }

    public Observable<Response<Location>> postLocationToServer(Location location) {
        String accessToken = mPrefsHelper.getAuthAccessToken();
        String client = mPrefsHelper.getAuthClient();
        String uid = mPrefsHelper.getAuthUid();
        CreateLocationRequest request = new CreateLocationRequest(location);
        return mAsrService.postLocationWithObservable(accessToken, client, uid, request);
    }

    public void saveAuthorizationResponse(Response<SignInResponse> response) {
        mPrefsHelper.setAuthorization(response.headers());
        mPrefsHelper.setCurrentUser(response.body().getUser());
    }

    public boolean isLoggedWithToken() {
        return !mPrefsHelper.getAuthAccessToken().isEmpty();
    }

    public User getCurrentUser() {
        return mPrefsHelper.getCurrentUser();
    }
}
