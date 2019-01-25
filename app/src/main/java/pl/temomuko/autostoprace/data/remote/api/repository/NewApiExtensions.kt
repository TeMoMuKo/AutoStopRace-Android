package pl.temomuko.autostoprace.data.remote.api.repository

import android.net.Uri
import pl.temomuko.autostoprace.data.model.LocationRecord
import pl.temomuko.autostoprace.data.model.SignInResponse
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
    return locationRecord
}


fun List<TeamEntity>.toLegacyTeam(): List<Team> {
    return map {
        val lastLocation = it.lastLocation?.toLocationRecord()
        Team("team-${it.number}", "Team ${it.number}", lastLocation)
    }
}

fun UserEntity.toLegacyUser(): User {
    return User(id, 0, firstName, lastName, email)
}