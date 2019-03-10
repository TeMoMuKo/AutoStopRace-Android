package pl.temomuko.autostoprace.domain.repository

import pl.temomuko.autostoprace.domain.model.Team
import pl.temomuko.autostoprace.data.remote.AsrService
import rx.Single
import javax.inject.Inject

class TeamsRepository @Inject constructor(
    private val asrService: AsrService
) {

    fun getAllTeams(): Single<List<Team>> {
        return asrService.getAllTeams()
            .map { it.toTeam() }
    }
}
