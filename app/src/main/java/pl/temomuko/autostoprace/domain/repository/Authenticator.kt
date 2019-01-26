package pl.temomuko.autostoprace.domain.repository

import android.util.Base64
import pl.temomuko.autostoprace.data.local.PrefsHelper
import pl.temomuko.autostoprace.domain.model.User
import pl.temomuko.autostoprace.data.remote.api.Asr2019Service
import pl.temomuko.autostoprace.data.remote.api.model.UserEntity
import retrofit2.Response
import rx.Completable
import rx.Single
import javax.inject.Inject

class Authenticator @Inject constructor(
    private val asr2019Service: Asr2019Service,
    private val prefsHelper: PrefsHelper
) {
    val token: String
        get() = prefsHelper.authAccessToken

    fun authorize(email: String, password: String): Single<User> {
        val encodedCredentials = Base64.encodeToString("$email:$password".toByteArray(), Base64.NO_WRAP)
        val basicAuthHeaderValue = "Basic $encodedCredentials"
        return asr2019Service.authorize(basicAuthHeaderValue)
            .doOnSuccess { saveAuthToken(it) }
            .map { it.body()!!.toUser() }
    }

    private fun saveAuthToken(response: Response<UserEntity>) {
        val authTokenHeader = response.headers()["X-Auth-Token"]
        authTokenHeader?.let { prefsHelper.authAccessToken = it }
    }

    fun resetPassword(email: String): Completable {
        return asr2019Service.resetPassword(email)
    }

    fun validateToken(): Single<User> {
        return asr2019Service.validateToken()
            .map { it.toUser() }
    }

    fun logout(): Completable {
        return asr2019Service.logout()
    }
}