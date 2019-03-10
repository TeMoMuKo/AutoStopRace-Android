package pl.temomuko.autostoprace

import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun initFlavorConfig(context: Context) {
    Stetho.initializeWithDefaults(context)
}

fun OkHttpClient.Builder.addFlavorInterceptors(): OkHttpClient.Builder {
    val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    addInterceptor(httpLoggingInterceptor)
    return addNetworkInterceptor(StethoInterceptor())
}
