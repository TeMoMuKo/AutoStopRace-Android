package pl.temomuko.autostoprace.data.remote;

import java.util.List;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.CreateLocationRecordRequest;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.ResetPassResponse;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.Team;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public interface AsrService {

    @GET("teams")
    Observable<Response<List<Team>>> getAllTeams();

    @GET("teams/team-{team_number}")
    Observable<Response<Team>> getTeam(@Path("team_number") int teamNumber);

    @GET("teams/team-{team_number}/locations")
    Observable<Response<List<LocationRecord>>> getLocationRecords(@Path("team_number") int teamNumber);

    @FormUrlEncoded
    @POST("api/v1/auth/sign_in")
    Observable<Response<SignInResponse>> signIn(
            @Field("email") String email,
            @Field("password") String password
    );

    @DELETE("api/v1/auth/sign_out")
    Observable<Response<SignOutResponse>> signOut(
            @Header(Constants.HEADER_FIELD_TOKEN) String accessToken,
            @Header(Constants.HEADER_FIELD_CLIENT) String client,
            @Header(Constants.HEADER_FIELD_UID) String uid
    );

    @GET("api/v1/auth/validate_token")
    Observable<Response<SignInResponse>> validateToken(
            @Header(Constants.HEADER_FIELD_TOKEN) String accessToken,
            @Header(Constants.HEADER_FIELD_CLIENT) String client,
            @Header(Constants.HEADER_FIELD_UID) String uid
    );

    @FormUrlEncoded
    @POST("api/v1/auth/password")
    Observable<Response<ResetPassResponse>> resetPassword(
            @Field("email") String email,
            @Field("redirect_url") String redirectUrl
    );

    @Headers("Content-Type: " + Constants.HEADER_VALUE_APPLICATION_JSON)
    @POST("locations")
    Observable<Response<LocationRecord>> postLocationRecord(
            @Header(Constants.HEADER_FIELD_TOKEN) String accessToken,
            @Header(Constants.HEADER_FIELD_CLIENT) String client,
            @Header(Constants.HEADER_FIELD_UID) String uid,
            @Body CreateLocationRecordRequest request
    );
}