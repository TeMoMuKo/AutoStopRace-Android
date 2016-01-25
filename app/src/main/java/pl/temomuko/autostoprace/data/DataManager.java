package pl.temomuko.autostoprace.data;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.remote.ApiManager;
import retrofit.Response;
import rx.Observable;

/**
 * Created by szymen on 2016-01-09.
 */

@Singleton
public class DataManager {

    private ApiManager mApiManager;
    private PrefsHelper mPrefsHelper;

    @Inject
    public DataManager(ApiManager apiManager, PrefsHelper prefsHelper) {
        mApiManager = apiManager;
        mPrefsHelper = prefsHelper;
    }

    public Observable<List<Location>> getTeamLocationsFromServer() {
        //TODO get current user team ID from API, if offline get form DB
        int teamId = 1;
        return mApiManager.getLocationsWithObservable(teamId);
    }

    public Observable<List<Location>> saveLocationsToDatabase(List<Location> locations) {
        //TODO save locations from server to DB with DatabaseHelper and return Observable with locations from DB.
        return Observable.just(locations);
    }

    public Observable<Response> signIn(String login, String password) {
        return mApiManager.signInWithObservable(login, password);
    }

    public void saveAuthorizationResponse(Response response) {
        //TODO save headers with access-token, client, uid to prefs.
    }
}
