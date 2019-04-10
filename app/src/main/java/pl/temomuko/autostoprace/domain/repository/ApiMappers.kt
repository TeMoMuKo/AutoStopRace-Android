package pl.temomuko.autostoprace.domain.repository

import android.net.Uri
import pl.temomuko.autostoprace.data.remote.model.*
import pl.temomuko.autostoprace.domain.model.*

fun LocationEntity.toLocationRecord(): LocationRecord {
    val uri = imageUrl?.let { Uri.parse(it) }
    val locationRecord = LocationRecord(
        latitude,
        longitude,
        message,
        address,
        countryName,
        countryCode,
        uri
    )
    locationRecord.serverReceiptDate = this.createdAt
    locationRecord.id = this.id.toInt()
    return locationRecord
}

fun List<TeamEntity>.toTeam(): List<Team> {
    return map {
        val lastLocation = it.lastLocation?.toLocationRecord()
        Team(
            teamNumber = it.number,
            lastLocation = lastLocation
        )
    }
}

fun UserEntity.toUser(): User {
    return User(
        id = id,
        teamNumber = teamNumber,
        firstName = firstName,
        lastName = lastName,
        email = email
    )
}

fun RaceInfoImagesEntity.toRaceInfoImages(): RaceInfoImages {
    return RaceInfoImages(
        scheduleImageUrl = scheduleImageUrl,
        campusMapImageUrl = campusMapImageUrl
    )
}

fun CompetitionEntity.toCompetition(): Competition {
    return Competition(
        id = id,
        name = name,
        description = description
    )
}

fun List<CompetitionEntity>.toCompetitions(): List<Competition> {
    return map { it.toCompetition() }
}
