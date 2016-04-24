package pl.temomuko.autostoprace.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.temomuko.autostoprace.BuildConfig;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.ErrorResponse;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Szymon Kozak on 2016-04-15.
 */

@Singleton
public class ApiManager {

    private final Retrofit mRetrofit;
    private final AsrService mAsrService;

    @Inject
    public ApiManager() {
        mRetrofit = getRetrofit();
        mAsrService = mRetrofit.create(AsrService.class);
    }

    private Retrofit getRetrofit() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new GmtDateDeserializer())
                .create();
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public AsrService getAsrService() {
        return mAsrService;
    }

    public Converter<ResponseBody, ErrorResponse> getErrorResponseConverter() {
        return mRetrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Constants.HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
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
                .setLevel(BuildConfig.DEBUG ?
                        HttpLoggingInterceptor.Level.BODY :
                        HttpLoggingInterceptor.Level.NONE);
    }
}
