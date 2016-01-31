package pl.temomuko.autostoprace.data.remote;

import java.util.List;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.CreateLocationRequest;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.Team;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
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

    @GET("teams")
    Observable<Response<List<Team>>> getTeamsWithObservable();

    @GET("teams/{team_id}")
    Observable<Response<Team>> getTeamWithObservable(@Path("team_id") int teamId);

    @GET("teams/{team_id}/locations")
    Observable<Response<List<Location>>> getLocationsWithObservable(@Path("team_id") int teamId);

    @FormUrlEncoded
    @POST("auth/sign_in")
    Observable<Response<SignInResponse>> signInWithObservable(
            @Field("email") String email,
            @Field("password") String password
    );

    @DELETE("auth/sign_out")
    Observable<Response<SignOutResponse>> signOutWithObservable(
            @Header(Constants.HEADER_FIELD_TOKEN) String accessToken,
            @Header(Constants.HEADER_FIELD_CLIENT) String client,
            @Header(Constants.HEADER_FIELD_UID) String uid
    );

    @GET("auth/validate_token")
    Observable<Response<SignInResponse>> validateTokenWithObservable(
            @Header(Constants.HEADER_FIELD_TOKEN) String accessToken,
            @Header(Constants.HEADER_FIELD_CLIENT) String client,
            @Header(Constants.HEADER_FIELD_UID) String uid
    );

    @Headers("Content-Type: " + Constants.HEADER_CONTENT_TYPE_JSON)
    @POST("locations")
    Observable<Response<Location>> postLocationWithObservable (
            @Header(Constants.HEADER_FIELD_TOKEN) String accessToken,
            @Header(Constants.HEADER_FIELD_CLIENT) String client,
            @Header(Constants.HEADER_FIELD_UID) String uid,
            @Body CreateLocationRequest request
    );
}
