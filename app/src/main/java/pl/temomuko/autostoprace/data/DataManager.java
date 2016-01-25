package pl.temomuko.autostoprace.data;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.remote.ApiManager;
import rx.Observable;

/**
 * Created by szymen on 2016-01-09.
 */

@Singleton
public class DataManager {

    private ApiManager mApiManager;

    @Inject
    public DataManager(ApiManager apiManager) {
        mApiManager = apiManager;
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
}
