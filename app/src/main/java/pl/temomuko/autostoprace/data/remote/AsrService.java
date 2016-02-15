package pl.temomuko.autostoprace.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.temomuko.autostoprace.BuildConfig;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.CreateLocationRecordRequest;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.Team;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
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
 * Created by szymen on 2016-01-22.
 */
public interface AsrService {

    @GET("teams")
    Observable<Response<List<Team>>> getTeamsWithObservable();

    @GET("teams/{team_id}")
    Observable<Response<Team>> getTeamWithObservable(@Path("team_id") int teamId);

    @GET("teams/{team_id}/locations")
    Observable<Response<List<LocationRecord>>> getLocationRecordsWithObservable(@Path("team_id") int teamId);

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
    Observable<Response<LocationRecord>> postLocationRecordWithObservable(
            @Header(Constants.HEADER_FIELD_TOKEN) String accessToken,
            @Header(Constants.HEADER_FIELD_CLIENT) String client,
            @Header(Constants.HEADER_FIELD_UID) String uid,
            @Body CreateLocationRecordRequest request
    );

    class Factory {

        public static AsrService createAsrService() {
            Gson gson = new GsonBuilder()
                    .setDateFormat(Constants.JSON_DATE_FORMAT)
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(AsrService.class);
        }

        private static OkHttpClient getOkHttpClient() {
            return new OkHttpClient.Builder()
                    .addInterceptor(getLoggingInterceptor())
                    .addNetworkInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept", Constants.HEADER_ACCEPT_JSON)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();
        }

        private static HttpLoggingInterceptor getLoggingInterceptor() {
            return new HttpLoggingInterceptor()
                    .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                            : HttpLoggingInterceptor.Level.NONE);
        }
    }
}