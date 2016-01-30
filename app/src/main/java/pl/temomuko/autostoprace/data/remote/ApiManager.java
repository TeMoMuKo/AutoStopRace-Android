package pl.temomuko.autostoprace.data.remote;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.CreateLocationRequest;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 * Created by szymen on 2016-01-22.
 */

@Singleton
public class ApiManager {

    private AsrService mAsrService;

    @Inject
    public ApiManager() {
        mAsrService = getRetrofit().create(AsrService.class);
    }

    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @NonNull
    private Gson getGson() {
        return new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient okhttp = new OkHttpClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttp.interceptors().add(interceptor);
        okhttp.networkInterceptors().add(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("Accept", Constants.HEADER_ACCEPT_JSON)
                    .build();
            return chain.proceed(request);
        });
        return okhttp;
    }

    public Observable<Response<List<Location>>> getLocationsWithObservable(int teamId) {
        return mAsrService.getLocationsWithObservable(teamId);
    }

    public Observable<Response<SignInResponse>> signInWithObservable(String email, String password) {
        return mAsrService.signInWithObservable(email, password);
    }

    public Observable<Response<SignOutResponse>> signOutWithObservable(
            String accessToken, String client, String uid) {
        return mAsrService.signOutWithObservable(accessToken, client, uid);
    }

    public Observable<Response<Location>> postLocationWithObservable(
            String accessToken, String client, String uid, CreateLocationRequest request) {
        return mAsrService.postLocationWithObservable(accessToken, client, uid, request);
    }
}
