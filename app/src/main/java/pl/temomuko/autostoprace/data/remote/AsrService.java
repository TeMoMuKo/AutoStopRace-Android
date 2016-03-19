package pl.temomuko.autostoprace.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.temomuko.autostoprace.BuildConfig;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.CreateLocationRecordRequest;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.ResetPassResponse;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.Team;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
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
    Observable<Response<List<Team>>> getTeams();

    @GET("teams/{team_id}")
    Observable<Response<Team>> getTeam(@Path("team_id") int teamId);

    @GET("teams/{team_id}/locations")
    Observable<Response<List<LocationRecord>>> getLocationRecords(@Path("team_id") int teamId);

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

    class Factory {

        private static Converter<ResponseBody, SignInResponse> SIGN_IN_ERROR_RESPONSE_CONVERTER;
        private static Converter<ResponseBody, ResetPassResponse> RESET_PASS_ERROR_RESPONSE_CONVERTER;

        public static AsrService createAsrService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new GmtDateDeserializer())
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            setupErrorsConverters(retrofit);
            return retrofit.create(AsrService.class);
        }

        private static void setupErrorsConverters(Retrofit retrofit) {
            SIGN_IN_ERROR_RESPONSE_CONVERTER = retrofit
                    .responseBodyConverter(SignInResponse.class, new Annotation[0]);
            RESET_PASS_ERROR_RESPONSE_CONVERTER = retrofit
                    .responseBodyConverter(ResetPassResponse.class, new Annotation[0]);
        }

        public static Converter<ResponseBody, SignInResponse> getSignInErrorResponseConverter() {
            return SIGN_IN_ERROR_RESPONSE_CONVERTER;
        }

        public static Converter<ResponseBody, ResetPassResponse> getResetPassErrorResponseConverter() {
            return RESET_PASS_ERROR_RESPONSE_CONVERTER;
        }

        private static OkHttpClient getOkHttpClient() {
            return new OkHttpClient.Builder()
                    .addInterceptor(getLoggingInterceptor())
                    .addNetworkInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON)
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