package pl.temomuko.autostoprace.domain.repository

import pl.temomuko.autostoprace.data.local.Preferences
import pl.temomuko.autostoprace.data.remote.AsrService
import pl.temomuko.autostoprace.domain.model.Competition
import pl.temomuko.autostoprace.domain.model.RaceInfoImages
import rx.Single
import javax.inject.Inject

class RaceInfoRepository @Inject constructor(
    private val asrService: AsrService,
    private val preferences: Preferences
) {

    fun getRaceInfoImages(): Single<RaceInfoImages> {
        return asrService.getRaceInfoImages()
            .map { it.toRaceInfoImages() }
            .doOnSuccess {
                preferences.raceInfoScheduleUrl = it.scheduleImageUrl
                preferences.raceInfoCampusMapUrl = it.campusMapImageUrl
            }
            .onErrorReturn {
                RaceInfoImages(
                    scheduleImageUrl = preferences.raceInfoScheduleUrl,
                    campusMapImageUrl = preferences.raceInfoCampusMapUrl
                )
            }
    }

    fun getCompetitions(): Single<List<Competition>> {
        return asrService.getCompetitions()
            .map { it.toCompetitions() }
    }
}
