package pl.temomuko.autostoprace.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Team(
    val teamNumber: Long,
    val lastLocation: LocationRecord?
) : Comparable<Team>, Parcelable {

    override fun compareTo(other: Team): Int {
        return teamNumber.compareTo(other.teamNumber)
    }
}