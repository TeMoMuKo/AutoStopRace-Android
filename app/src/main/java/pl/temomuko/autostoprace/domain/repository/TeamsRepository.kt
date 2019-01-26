package pl.temomuko.autostoprace.domain.repository

import pl.temomuko.autostoprace.domain.model.Team
import pl.temomuko.autostoprace.data.remote.api.Asr2019Service
import rx.Single
import javax.inject.Inject

class TeamsRepository @Inject constructor(
    private val asr2019Service: Asr2019Service
) {

    fun getAllTeams(): Single<List<Team>> {
        return asr2019Service.getAllTeams()
            .map { it.toTeam() }
    }
}
