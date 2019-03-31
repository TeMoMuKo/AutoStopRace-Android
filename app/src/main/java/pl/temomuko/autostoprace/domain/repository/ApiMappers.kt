package pl.temomuko.autostoprace.domain.repository

import android.net.Uri
import pl.temomuko.autostoprace.data.remote.model.LocationEntity
import pl.temomuko.autostoprace.data.remote.model.RaceInfoImagesEntity
import pl.temomuko.autostoprace.data.remote.model.TeamEntity
import pl.temomuko.autostoprace.data.remote.model.UserEntity
import pl.temomuko.autostoprace.domain.model.LocationRecord
import pl.temomuko.autostoprace.domain.model.RaceInfoImages
import pl.temomuko.autostoprace.domain.model.Team
import pl.temomuko.autostoprace.domain.model.User

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
