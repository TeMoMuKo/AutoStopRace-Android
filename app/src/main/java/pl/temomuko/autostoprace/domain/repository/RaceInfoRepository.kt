package pl.temomuko.autostoprace.domain.repository

import pl.temomuko.autostoprace.data.remote.AsrService
import pl.temomuko.autostoprace.domain.model.RaceInfoImages
import rx.Single
import javax.inject.Inject

class RaceInfoRepository @Inject constructor(
    private val asrService: AsrService
) {

    fun getRaceInfoImages(): Single<RaceInfoImages> {
        return asrService.getRaceInfoImages()
            .map { it.toRaceInfoImages() }
    }
}
