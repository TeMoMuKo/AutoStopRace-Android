package pl.temomuko.autostoprace.domain.repository

import android.util.Base64
import pl.temomuko.autostoprace.data.local.PrefsHelper
import pl.temomuko.autostoprace.data.remote.ApiException
import pl.temomuko.autostoprace.data.remote.AsrService
import pl.temomuko.autostoprace.data.remote.TokenAuthenticationInterceptor
import pl.temomuko.autostoprace.data.remote.model.UserEntity
import pl.temomuko.autostoprace.domain.model.User
import retrofit2.Response
import rx.Completable
import rx.Single
import javax.inject.Inject

class Authenticator @Inject constructor(
    private val asrService: AsrService,
    private val prefsHelper: PrefsHelper
) {
    val token: String
        get() = prefsHelper.authAccessToken

    fun authorize(email: String, password: String): Single<User> {
        val encodedCredentials =
            Base64.encodeToString("$email:$password".toByteArray(), Base64.NO_WRAP)
        val basicAuthHeaderValue = "Basic $encodedCredentials"
        return asrService.authorize(basicAuthHeaderValue)
            .doOnSuccess { saveAuthToken(it) }
            .map {
                if (it.isSuccessful) {
                    it.body()!!.toUser()
                } else {
                    throw ApiException(it.code())
                    //todo handle errors in rx java call adapter
                }
            }
    }

    private fun saveAuthToken(response: Response<UserEntity>) {
        val authTokenHeader = response.headers()[TokenAuthenticationInterceptor.TOKEN_HEADER]
        authTokenHeader?.let { prefsHelper.authAccessToken = it }
    }

    fun validateToken(): Single<User> {
        return asrService.validateToken()
            .map { it.toUser() }
    }

    fun logout(): Completable {
        return asrService.logout()
    }
}
