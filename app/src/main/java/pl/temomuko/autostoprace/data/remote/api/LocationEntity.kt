package pl.temomuko.autostoprace.data.remote.api

import java.util.*

data class LocationEntity(
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val countryCode: String?,
    val imageUrl: String?,
    val message: String?,
    val createdAt: Date,
    val id: Int
)