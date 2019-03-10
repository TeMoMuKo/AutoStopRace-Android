package pl.temomuko.autostoprace

import android.content.Context
import okhttp3.OkHttpClient

fun initFlavorConfig(context: Context) = Unit

fun OkHttpClient.Builder.addFlavorInterceptors() = this
