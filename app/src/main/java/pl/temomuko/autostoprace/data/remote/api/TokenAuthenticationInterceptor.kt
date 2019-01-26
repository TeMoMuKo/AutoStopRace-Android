package pl.temomuko.autostoprace.data.remote.api

import dagger.Lazy
import okhttp3.Interceptor
import okhttp3.Response
import pl.temomuko.autostoprace.data.remote.api.repository.Authenticator
import javax.inject.Inject

class TokenAuthenticationInterceptor @Inject constructor(
    private val lazyAuthenticator: Lazy<Authenticator>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authToken = lazyAuthenticator.get().token
        return if (authToken.isNotBlank()) {
            val newRequest = originalRequest.newBuilder()
                .header("X-Auth-Token", lazyAuthenticator.get().token)
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}