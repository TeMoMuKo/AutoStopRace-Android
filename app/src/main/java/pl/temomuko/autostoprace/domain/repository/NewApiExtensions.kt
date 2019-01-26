package pl.temomuko.autostoprace.domain.repository

import android.net.Uri
import pl.temomuko.autostoprace.domain.model.LocationRecord
import pl.temomuko.autostoprace.domain.model.Team
import pl.temomuko.autostoprace.domain.model.User
import pl.temomuko.autostoprace.data.remote.model.LocationEntity
import pl.temomuko.autostoprace.data.remote.model.TeamEntity
import pl.temomuko.autostoprace.data.remote.model.UserEntity

fun LocationEntity.toLocationRecord(): LocationRecord {
    val uri = imageUrl?.let { Uri.parse(it) }
    val locationRecord = LocationRecord(
        latitude,
        longitude,
        message,
        address,
        countryCode,
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
        Team(it.number, lastLocation)
    }
}

fun UserEntity.toUser(): User {
    return User(
        id.toInt(),
        teamNumber.toInt(),
        firstName,
        lastName,
        email
    )
}