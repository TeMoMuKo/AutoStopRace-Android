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

        if (originalRequest.url().toString().contains("/me")) {
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .header("X-Auth-Token", lazyAuthenticator.get().token)
            .build()

        return chain.proceed(newRequest)
    }
}