package pl.temomuko.autostoprace.data;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.model.CreateLocationRequest;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.AsrService;
import retrofit.Response;
import rx.Observable;

/**
 * Created by szymen on 2016-01-09.
 */

@Singleton
public class DataManager {

    private AsrService mAsrService;
    private PrefsHelper mPrefsHelper;

    @Inject
    public DataManager(AsrService asrService, PrefsHelper prefsHelper) {
        mAsrService = asrService;
        mPrefsHelper = prefsHelper;
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

    public void clearAuth() {
        mPrefsHelper.clearAuth();
    }

    public Observable<Response<SignInResponse>> validateToken() {
        String accessToken = mPrefsHelper.getAuthAccessToken();
        String client = mPrefsHelper.getAuthClient();
        String uid = mPrefsHelper.getAuthUid();
        return mAsrService.validateTokenWithObservable(accessToken, client, uid);
    }

    public Observable<Response<List<Location>>> getTeamLocationsFromServer() {
        return mAsrService.getLocationsWithObservable(mPrefsHelper.getCurrentUser().getTeamId());
    }

    public Observable<List<Location>> saveLocationsToDatabase(List<Location> locations) {
        //TODO save locations from server to DB with DatabaseHelper and return Observable with locations from DB.
        return Observable.just(locations);
    }

    public void saveLocationToDatabase(Location location) {
        //TODO
    }

    public Observable<Response<Location>> postLocationToServer(CreateLocationRequest request) {
        String accessToken = mPrefsHelper.getAuthAccessToken();
        String client = mPrefsHelper.getAuthClient();
        String uid = mPrefsHelper.getAuthUid();
        return mAsrService.postLocationWithObservable(accessToken, client, uid, request);
    }

    public void saveAuthorizationResponse(Response<SignInResponse> response) {
        Map<String, List<String>> headers = response.headers().toMultimap();
        mPrefsHelper.setAuthAccessToken(headers.get(Constants.HEADER_FIELD_TOKEN).get(0));
        mPrefsHelper.setAuthClient(headers.get(Constants.HEADER_FIELD_CLIENT).get(0));
        mPrefsHelper.setAuthUid(headers.get(Constants.HEADER_FIELD_UID).get(0));
        mPrefsHelper.setCurrentUser(response.body().getUser());
    }

    public boolean isLoggedWithToken() {
        return !mPrefsHelper.getAuthAccessToken().equals("");
    }

    public User getCurrentUser() {
        return mPrefsHelper.getCurrentUser();
    }
}
