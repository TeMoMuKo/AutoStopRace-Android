package pl.temomuko.autostoprace.data.remote.api.repository

import android.net.Uri
import pl.temomuko.autostoprace.data.model.LocationRecord
import pl.temomuko.autostoprace.data.model.Team
import pl.temomuko.autostoprace.data.model.User
import pl.temomuko.autostoprace.data.remote.api.LocationEntity
import pl.temomuko.autostoprace.data.remote.api.TeamEntity
import pl.temomuko.autostoprace.data.remote.api.UserEntity

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


fun List<TeamEntity>.toLegacyTeam(): List<Team> {
    return map {
        val lastLocation = it.lastLocation?.toLocationRecord()
        Team(it.number, lastLocation)
    }
}

fun UserEntity.toLegacyUser(): User {
    return User(id.toInt(), teamNumber.toInt(), firstName, lastName, email)
}