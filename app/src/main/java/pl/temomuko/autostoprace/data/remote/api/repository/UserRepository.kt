package pl.temomuko.autostoprace.data.remote.api.repository

import pl.temomuko.autostoprace.data.remote.api.Asr2019Service
import rx.Completable
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val asr2019Service: Asr2019Service
) {

    fun authorize(): Completable {
        return asr2019Service.authorize().toCompletable()
    }
}