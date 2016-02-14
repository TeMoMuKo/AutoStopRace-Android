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
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
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
        return mAsrService.signOutWithObservable(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid());
    }

    public void clearUserData() {
        mPrefsHelper.clearAuth();
        mDatabaseManager.clearTables().subscribe();
    }

    public Observable<Response<SignInResponse>> validateToken() {
        return mAsrService.validateTokenWithObservable(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid());
    }

    public Observable<List<Location>> getTeamLocationsFromDatabase() {
        return Observable.zip(
                mDatabaseManager.getUnsentLocationList(),
                mDatabaseManager.getSentLocationList(),
                (unsentLocations, sentLocations) -> {
                    ArrayList<Location> result = new ArrayList<>(sentLocations);
                    result.addAll(unsentLocations);
                    return result;
                }
        );
    }

    public Observable<Response<List<Location>>> getTeamLocationsFromServer() {
        return mAsrService.getLocationsWithObservable(mPrefsHelper.getCurrentUser().getTeamId());
    }

    public Observable<List<Location>> syncWithDatabase(Response<List<Location>> response) {
        return response.code() == HttpStatus.OK ?
                saveAndEmitLocationsFromDatabase(response.body()) :
                Observable.error(new StandardResponseException(response));
    }

    private Observable<List<Location>> saveAndEmitLocationsFromDatabase(List<Location> receivedLocations) {
        return Observable.zip(
                mDatabaseManager.getUnsentLocationList(),
                mDatabaseManager.setAndEmitReceivedLocationList(receivedLocations),
                (unsentLocations, sentLocations) -> {
                    ArrayList<Location> result = new ArrayList<>(sentLocations);
                    result.addAll(unsentLocations);
                    return result;
                }
        );
    }

    public Observable<Response<SignInResponse>> handleLoginResponse(Response<SignInResponse> response) {
        return response.code() == HttpStatus.OK ?
                Observable.just(response) :
                Observable.error(new StandardResponseException(response));
    }

    public Observable<Location> handlePostLocationResponse(Response<Location> response) {
        return response.code() == HttpStatus.CREATED ?
                Observable.just(response.body()) :
                Observable.error(new StandardResponseException(response));
    }

    public Observable<Void> saveSentLocationToDatabase(Location location) {
        return mDatabaseManager.addSentLocation(location);
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
        return mAsrService.postLocationWithObservable(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid(),
                new CreateLocationRequest(location));
    }

    public void saveAuthorizationResponse(Response<SignInResponse> response) {
        mPrefsHelper.setAuthorizationHeaders(response.headers());
        mPrefsHelper.setCurrentUser(response.body().getUser());
    }

    public boolean isLoggedWithToken() {
        return !mPrefsHelper.getAuthAccessToken().isEmpty();
    }

    public User getCurrentUser() {
        return mPrefsHelper.getCurrentUser();
    }
}
