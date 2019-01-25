package pl.temomuko.autostoprace.data.remote.api.repository

import android.net.Uri
import pl.temomuko.autostoprace.data.model.LocationRecord
import pl.temomuko.autostoprace.data.model.Team
import pl.temomuko.autostoprace.data.remote.api.Asr2019Service
import pl.temomuko.autostoprace.data.remote.api.LocationEntity
import pl.temomuko.autostoprace.data.remote.api.TeamEntity
import rx.Single
import javax.inject.Inject

class TeamsRepository @Inject constructor(
    private val asr2019Service: Asr2019Service
) {

    fun getAllTeams(): Single<List<Team>> {
        return asr2019Service.getAllTeams()
            .map { it.toLegacyTeam() }

    }
}

private fun List<TeamEntity>.toLegacyTeam(): List<Team> {
    return map {
        val lastLocation = it.lastLocation?.toLocationRecord()
        Team("team-${it.number}", "Team ${it.number}", lastLocation)
    }
}

private fun LocationEntity.toLocationRecord(): LocationRecord {
   val uri = imageUrl?.let { Uri.parse(it) }
    return LocationRecord(
        latitude,
        longitude,
        message,
        address,
        countryCode,
        countryCode,
        uri
    )
}
