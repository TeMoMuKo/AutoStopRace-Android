package pl.temomuko.autostoprace.injection.module

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import pl.temomuko.autostoprace.BuildConfig
import pl.temomuko.autostoprace.Constants
import pl.temomuko.autostoprace.addFlavorInterceptors
import pl.temomuko.autostoprace.data.remote.AsrService
import pl.temomuko.autostoprace.data.remote.GmtDateDeserializer
import pl.temomuko.autostoprace.data.remote.PostProcessingEnabler
import pl.temomuko.autostoprace.data.remote.TokenAuthenticationInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    fun provideAsr2019Service(retrofit: Retrofit) = retrofit.create<AsrService>()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, GmtDateDeserializer())
            .registerTypeAdapterFactory(PostProcessingEnabler())
            .create()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
    }

    @Provides
    fun provideOkHttp(tokenAuthenticationInterceptor: TokenAuthenticationInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(Constants.HTTP_CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Constants.HTTP_READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Constants.HTTP_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addNetworkInterceptor(tokenAuthenticationInterceptor)
            .addFlavorInterceptors()
            .addNetworkInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    private inline fun <reified T> Retrofit.create(): T {
        return create(T::class.java)
    }
}
