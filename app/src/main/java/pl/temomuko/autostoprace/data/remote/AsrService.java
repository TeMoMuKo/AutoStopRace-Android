package pl.temomuko.autostoprace.data.remote;

import java.util.List;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.model.request.CreateLocationRequest;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
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

    @POST("auth/sign_in")
    Observable<Response> signInWithObservable(
            @Field("email") String email,
            @Field("password") String password
    );

    @Headers("Content-Type: " + Constants.HEADER_CONTENT_TYPE_JSON)
    @POST("location")
    Observable<Response> postLocationWithObservable (
            @Header("access-token") String accessToken,
            @Header("client") String client,
            @Header("uid") String uid,
            @Body CreateLocationRequest request
    );
}
