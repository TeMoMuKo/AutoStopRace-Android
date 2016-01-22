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

    public Observable<List<Location>> getCurrentUserTeamLocations() {
        return mApiManager.getLocationsWithObservable(1);
    }
}
