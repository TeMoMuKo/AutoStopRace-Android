package pl.temomuko.autostoprace.data.remote;

import java.util.List;

import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.data.model.User;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by szymen on 2016-01-22.
 */
public interface AsrService {

    @GET("user")
    Observable<User> getCurrentUserWithObservable();

    @GET("teams")
    Observable<List<Team>> getTeamsWithObservable();

    @GET("teams/{team_id}")
    Observable<Team> getTeamWithObservable(@Path("team_id") int teamId);

    @GET("teams/{team_id}/locations")
    Observable<List<Location>> getLocationsWithObservable(@Path("team_id") int teamId);
}
